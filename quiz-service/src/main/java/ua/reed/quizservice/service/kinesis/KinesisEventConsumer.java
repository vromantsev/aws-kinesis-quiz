package ua.reed.quizservice.service.kinesis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.GetRecordsResponse;
import software.amazon.awssdk.services.kinesis.model.ListShardsRequest;
import software.amazon.awssdk.services.kinesis.model.ListShardsResponse;
import ua.reed.quizservice.config.KinesisProperties;
import ua.reed.quizservice.service.event.GameOverEvent;
import ua.reed.quizservice.service.event.StatsEvent;
import ua.reed.quizservice.service.helper.GameHelperService;
import ua.reed.quizservice.service.processor.QuizProcessor;
import ua.reed.quizservice.service.stats.StatService;
import ua.reed.quizservice.utils.AwsClientUtils;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class KinesisEventConsumer implements EventConsumer {

    private static final int FINAL_SCORE = 1000;

    private final KinesisClient kinesisClient;
    private final EventSender eventSender;
    private final StatService statService;
    private final QuizProcessor quizProcessor;
    private final KinesisProperties kinesisProperties;
    private final GameHelperService gameHelperService;

    @Override
    public void consumeEvent(final String stream) {
        Objects.requireNonNull(stream, "Parameter [stream] must not be null!");
        if (gameHelperService.isGameOngoing()) {
            var shardsResponse = kinesisClient.listShards(ListShardsRequest.builder().streamName(stream).build());
            statService.getStats().stream()
                    .filter(stats -> stats.score() == FINAL_SCORE)
                    .findAny()
                    .ifPresentOrElse(
                            stats -> {
                                log.info("We have a winner! Congratulations, {} you scored {} points!", stats.playerName(), stats.score());
                                eventSender.sendEvent(
                                        GameOverEvent.builder().message("Game over!").stats(statService.getStats()).build(),
                                        kinesisProperties.getAnswersStream()
                                );
                                gameHelperService.stopGame();
                            },
                            () -> checkAnswers(stream, shardsResponse)
                    );
            this.eventSender.sendEvent(
                    StatsEvent.builder().stats(statService.getStats()).build(), this.kinesisProperties.getStatsStream()
            );
        } else {
            log.warn("Game is over! Stats: {}", statService.getStats());
        }
    }

    private void checkAnswers(final String stream, final ListShardsResponse shardsResponse) {
        for (var shard : shardsResponse.shards()) {
            var shardIterator = kinesisClient.getShardIterator(AwsClientUtils.createShardIteratorRequest(stream, shard)).shardIterator();
            processRecords(shardIterator);
        }
    }

    private void processRecords(final String shardIterator) {
        if (shardIterator != null) {
            GetRecordsResponse getRecordsResponse = kinesisClient.getRecords(AwsClientUtils.createGetRecordsRequest(shardIterator));
            var records = getRecordsResponse.records();
            if (!records.isEmpty()) {
                log.info("Received {} records from shard iterator {}", records.size(), shardIterator);
                this.quizProcessor.process(records);
                String nextShardIterator = getRecordsResponse.nextShardIterator();
                if (nextShardIterator != null) {
                    log.info("Processing next shard iterator {}", nextShardIterator);
                    processRecords(nextShardIterator);
                }
            } else {
                log.info("Received no records from shard iterator {}", shardIterator);
            }
        }
    }
}