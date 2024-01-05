package ua.reed.playerservice.service.kinesis;

public interface EventConsumer {

    void consumeEvent(final String stream);
    
}
