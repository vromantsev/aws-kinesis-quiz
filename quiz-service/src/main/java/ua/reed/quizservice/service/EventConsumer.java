package ua.reed.quizservice.service;

public interface EventConsumer {

    void consumeEvent(final String stream);
    
}
