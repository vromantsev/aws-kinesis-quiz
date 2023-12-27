package ua.reed.playerservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.kinesis.model.GetShardIteratorRequest;
import software.amazon.awssdk.services.kinesis.model.Record;
import software.amazon.awssdk.services.kinesis.model.Shard;
import software.amazon.awssdk.services.kinesis.model.ShardIteratorType;
import ua.reed.playerservice.service.event.GameOverEvent;
import ua.reed.playerservice.service.event.StatsEvent;

import java.io.IOException;
import java.util.Optional;

@Slf4j
public abstract class AbstractEventConsumer implements EventConsumer {

    protected final ObjectMapper objectMapper;
    protected final GameHelperService gameHelperService;

    protected AbstractEventConsumer(final ObjectMapper objectMapper, final GameHelperService gameHelperService) {
        this.objectMapper = objectMapper;
        this.gameHelperService = gameHelperService;
    }

    public abstract void consumeEvent(String stream);

    protected GetShardIteratorRequest createShardIteratorRequest(final String stream, final Shard shard) {
        return GetShardIteratorRequest.builder()
                .streamName(stream)
                .shardId(shard.shardId())
                .shardIteratorType(ShardIteratorType.TRIM_HORIZON)
                .build();
    }

    protected Optional<StatsEvent> getStatsFromEvent(final Record record) {
        try {
            return Optional.ofNullable(objectMapper.readValue(record.data().asByteArray(), StatsEvent.class));
        } catch (IOException e) {
            log.error("Event type is not StatsEvent");
            return Optional.empty();
        }
    }

    protected boolean getGameOverEvent(final Record record) {
        try {
            var gameOverEventOptional = Optional.ofNullable(objectMapper.readValue(record.data().asByteArray(), GameOverEvent.class))
                    .filter(e -> e.getMessage() != null && e.getStats() != null);
            if (gameOverEventOptional.isPresent()) {
                gameHelperService.stopGame();
                var gameOverEvent = gameOverEventOptional.get();
                log.info("We have a winner! Game stats: {}", gameOverEvent.getStats());
                return true;
            }
            return false;
        } catch (IOException e) {
            log.error("Event type is not GameOverEvent");
            return false;
        }
    }
}
