package ua.reed.playerservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.PutRecordRequest;
import ua.reed.playerservice.service.EventSender;
import ua.reed.playerservice.service.event.Event;

import java.nio.ByteBuffer;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class KinesisEventSender implements EventSender {

    private final KinesisClient kinesisClient;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public void sendEvent(final Event event, final String stream) {
        Objects.requireNonNull(event, "Parameter [event] must not be null!");
        Objects.requireNonNull(stream, "Parameter [stream] must not be null!");
        var payload = SdkBytes.fromByteBuffer(ByteBuffer.wrap(objectMapper.writeValueAsString(event).getBytes()));
        var request = PutRecordRequest.builder()
                .streamName(stream)
                .partitionKey(event.getPartitionKey().toString())
                .data(payload)
                .build();
        log.debug("Sending request to Kinesis stream: {}", request);
        var response = this.kinesisClient.putRecord(request);
        log.debug("Received response from Kinesis stream: {}", response);
    }
}
