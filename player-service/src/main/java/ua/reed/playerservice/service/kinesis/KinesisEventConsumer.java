package ua.reed.playerservice.service.kinesis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.GetRecordsRequest;
import software.amazon.awssdk.services.kinesis.model.ListShardsRequest;
import software.amazon.awssdk.services.kinesis.model.Record;
import ua.reed.playerservice.config.KinesisProperties;
import ua.reed.playerservice.model.Player;
import ua.reed.playerservice.repository.PlayerRepository;
import ua.reed.playerservice.service.helper.GameHelperService;
import ua.reed.playerservice.service.event.QuizAnswerEvent;
import ua.reed.playerservice.service.event.QuizQuestionEvent;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Component
public class KinesisEventConsumer extends AbstractEventConsumer {

    private static final int FINAL_SCORE = 1000;

    private final KinesisClient kinesisClient;
    private final KinesisProperties kinesisProperties;
    private final EventSender eventSender;
    private final PlayerRepository playerRepository;

    public KinesisEventConsumer(final ObjectMapper objectMapper,
                                final GameHelperService gameHelperService,
                                final KinesisClient kinesisClient,
                                final KinesisProperties kinesisProperties,
                                final EventSender eventSender,
                                final PlayerRepository playerRepository) {
        super(objectMapper, gameHelperService);
        this.kinesisClient = kinesisClient;
        this.kinesisProperties = kinesisProperties;
        this.eventSender = eventSender;
        this.playerRepository = playerRepository;
    }

    @SneakyThrows
    @Override
    public void consumeEvent(final String stream) {
        Objects.requireNonNull(stream, "Parameter [stream] must not be null!");
        processGameStats();
        if (gameHelperService.isGameOngoing()) {
            var records = getRecordsFromStream(stream);
            for (Record record : records) {
                boolean isGameOver = getGameOverEvent(record);
                if (isGameOver) {
                    break;
                }
                var quizQuestionEvent = objectMapper.readValue(record.data().asByteArray(), QuizQuestionEvent.class);
                var iterator = playerRepository.findAll().iterator();
                quizQuestionEvent.getOptions().forEach(option -> {
                    Player player = iterator.next();
                    sendPlayerAnswerEvent(option, quizQuestionEvent, player);
                });
            }
        } else {
            log.warn("Game is over!");
        }
    }

    private void processGameStats() {
        getRecordsFromStream(kinesisProperties.getStatsStream())
                .stream()
                .map(this::getStatsFromEvent)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .flatMap(statsEvent -> statsEvent.getStats().stream())
                .filter(gameStats -> gameStats.score() == FINAL_SCORE)
                .findAny()
                .ifPresentOrElse(
                        stats -> {
                            log.info("We have a winner! Congratulations, {} you scored {} points!", stats.playerName(), stats.score());
                            gameHelperService.stopGame();
                        },
                        () -> log.info("Game is still in progress, please be patient and give players some time to finish the quiz.")
                );
    }

    private List<Record> getRecordsFromStream(final String stream) {
        return this.kinesisClient.listShards(ListShardsRequest.builder().streamName(stream).build()).shards()
                .parallelStream()
                .map(shard -> kinesisClient.getShardIterator(createShardIteratorRequest(stream, shard)).shardIterator())
                .flatMap(shardIterator -> Stream.of(kinesisClient.getRecords(GetRecordsRequest.builder().shardIterator(shardIterator).limit(1).build()).records()))
                .reduce((l1, l2) -> {
                    l1.addAll(l2);
                    return l1;
                })
                .orElse(List.of());
    }

    private void sendPlayerAnswerEvent(final String option, QuizQuestionEvent quizQuestionEvent, Player player) {
        eventSender.sendEvent(
                QuizAnswerEvent.builder()
                        .question(quizQuestionEvent.getQuestion())
                        .answer(option)
                        .playerName(player.getName())
                        .build(),
                kinesisProperties.getAnswersStream()
        );
    }
}
