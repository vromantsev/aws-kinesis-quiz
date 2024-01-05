package ua.reed.quizservice.service.kinesis;

public interface EventConsumer {

    void consumeEvent(final String stream);
    
}
