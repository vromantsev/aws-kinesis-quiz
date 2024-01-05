package ua.reed.quizservice.service.processor;

import software.amazon.awssdk.services.kinesis.model.Record;

import java.util.List;

public interface QuizProcessor {

    void process(final List<Record> records);

}
