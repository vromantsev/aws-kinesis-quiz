package ua.reed.playerservice.service;

public interface EventConsumer {

    void consumeEvent(final String stream);
    
}
