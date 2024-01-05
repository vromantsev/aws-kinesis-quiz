package ua.reed.quizservice.service.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.kinesis.model.Record;
import ua.reed.quizservice.repository.PlayerRepository;
import ua.reed.quizservice.service.stats.StatService;
import ua.reed.quizservice.service.event.QuizAnswerEvent;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultQuizProcessor implements QuizProcessor {

    private final ObjectMapper objectMapper;
    private final PlayerRepository playerRepository;
    private final StatService statService;

    @SneakyThrows
    @Override
    public void process(final List<Record> records) {
        for (var record : records) {
            var quizAnswerEvent = objectMapper.readValue(record.data().asByteArray(), QuizAnswerEvent.class);
            var correct = statService.isAnswerCorrect(quizAnswerEvent.getAnswer());
            if (correct) {
                var player = playerRepository.updateScore(quizAnswerEvent.getPlayerName());
                log.info("Player {} answered correctly! Player's score is {}", quizAnswerEvent.getPlayerName(), player.getScore());
            } else {
                log.info("Player {} answered {}. Keep trying, pal!", quizAnswerEvent.getPlayerName(), quizAnswerEvent.getAnswer());
            }
        }
    }
}
