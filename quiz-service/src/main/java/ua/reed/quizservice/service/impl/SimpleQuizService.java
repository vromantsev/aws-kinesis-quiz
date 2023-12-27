package ua.reed.quizservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ua.reed.quizservice.config.KinesisProperties;
import ua.reed.quizservice.dto.RestartQuizRequest;
import ua.reed.quizservice.model.Question;
import ua.reed.quizservice.repository.QuestionRepository;
import ua.reed.quizservice.service.EventConsumer;
import ua.reed.quizservice.service.EventSender;
import ua.reed.quizservice.service.GameHelperService;
import ua.reed.quizservice.service.QuizService;
import ua.reed.quizservice.service.event.QuizQuestionEvent;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimpleQuizService implements QuizService {

    private static final String EVERY_TEN_SECONDS_CRON = "10 * * ? * *";
    private static final String EVERY_FIVE_SECONDS_CRON = "05 * * ? * *";

    private final EventSender eventSender;
    private final EventConsumer eventConsumer;
    private final KinesisProperties kinesisProperties;
    private final QuestionRepository questionRepository;
    private final GameHelperService gameHelperService;

    @Scheduled(cron = EVERY_TEN_SECONDS_CRON)
    @Override
    public void askQuestion() {
        var questions = pickRandomQuestion(questionRepository.getQuestions());
        this.eventSender.sendEvent(
                QuizQuestionEvent.builder()
                        .question(questions.question())
                        .options(questions.options())
                        .build(),
                kinesisProperties.getQuestionStream()
        );
    }

    @Scheduled(cron = EVERY_FIVE_SECONDS_CRON)
    @Override
    public void consumeAnswer() {
        eventConsumer.consumeEvent(kinesisProperties.getAnswersStream());
    }

    @Override
    public void restart(final RestartQuizRequest request) {
        if (request.restart()) {
            log.info("Restarting a quiz...");
            // TODO reload questions
            questionRepository.clearAskedQuestions();

            // TODO reset the flag
            gameHelperService.restartGame();
        }
    }

    private Question pickRandomQuestion(final List<Question> questions) {
        int randomQuestionNumber = ThreadLocalRandom.current().nextInt(questions.size());
        var randomQuestion = questions.get(randomQuestionNumber);
        questionRepository.updateAskedQuestions(randomQuestion);
        return randomQuestion;
    }
}
