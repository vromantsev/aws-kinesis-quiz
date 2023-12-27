package ua.reed.quizservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.GetRecordsRequest;
import software.amazon.awssdk.services.kinesis.model.GetRecordsResponse;
import software.amazon.awssdk.services.kinesis.model.GetShardIteratorRequest;
import software.amazon.awssdk.services.kinesis.model.ListShardsRequest;
import software.amazon.awssdk.services.kinesis.model.ListShardsResponse;
import software.amazon.awssdk.services.kinesis.model.Shard;
import software.amazon.awssdk.services.kinesis.model.ShardIteratorType;
import ua.reed.quizservice.config.KinesisProperties;
import ua.reed.quizservice.service.EventConsumer;
import ua.reed.quizservice.service.EventSender;
import ua.reed.quizservice.service.GameHelperService;
import ua.reed.quizservice.service.QuizProcessor;
import ua.reed.quizservice.service.StatService;
import ua.reed.quizservice.service.event.GameOverEvent;
import ua.reed.quizservice.service.event.StatsEvent;

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
            sendPlayerStatsEvent();
        } else {
            log.warn("Game is over! Stats: {}", statService.getStats());
        }
    }

    private void sendPlayerStatsEvent() {
        this.eventSender.sendEvent(
                StatsEvent.builder().stats(statService.getStats()).build(),
                this.kinesisProperties.getStatsStream()
        );
    }

    private void checkAnswers(final String stream, final ListShardsResponse shardsResponse) {
        for (var shard : shardsResponse.shards()) {
            var shardIterator = kinesisClient.getShardIterator(createShardIteratorRequest(stream, shard)).shardIterator();
            processRecords(shardIterator);
        }
    }

    private void processRecords(final String shardIterator) {
        if (shardIterator != null) {
            GetRecordsResponse getRecordsResponse = kinesisClient.getRecords(createGetRecordsRequest(shardIterator));
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

    private GetRecordsRequest createGetRecordsRequest(final String shardIterator) {
        return GetRecordsRequest.builder()
                .shardIterator(shardIterator)
                .limit(4)
                .build();
    }

    private GetShardIteratorRequest createShardIteratorRequest(final String stream, final Shard shard) {
        return GetShardIteratorRequest.builder()
                .streamName(stream)
                .shardId(shard.shardId())
                .shardIteratorType(ShardIteratorType.TRIM_HORIZON)
                .build();
    }
}