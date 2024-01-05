package ua.reed.playerservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ua.reed.playerservice.config.KinesisProperties;
import ua.reed.playerservice.service.kinesis.EventConsumer;

@Service
@RequiredArgsConstructor
public class SimpleQuizService implements QuizService {

    private static final String EVERY_FIVE_SECONDS_CRON = "05 * * ? * *";

    private final EventConsumer eventConsumer;
    private final KinesisProperties kinesisProperties;

    @Scheduled(cron = EVERY_FIVE_SECONDS_CRON)
    @Override
    public void answerQuestion() {
        this.eventConsumer.consumeEvent(kinesisProperties.getQuestionStream());
    }
}
