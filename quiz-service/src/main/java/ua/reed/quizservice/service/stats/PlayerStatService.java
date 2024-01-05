package ua.reed.quizservice.service.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.reed.quizservice.model.GameStats;
import ua.reed.quizservice.repository.PlayerRepository;
import ua.reed.quizservice.repository.QuestionRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerStatService implements StatService {

    private final PlayerRepository playerRepository;
    private final QuestionRepository questionRepository;

    @Override
    public boolean isAnswerCorrect(final String answer) {
        return questionRepository.getQuestions().stream().anyMatch(question -> question.answer().equals(answer));
    }

    @Override
    public List<GameStats> getStats() {
        var players = this.playerRepository.findAll();
        var stats = new ArrayList<GameStats>(players.size());
        players.forEach(player -> stats.add(new GameStats(player.getName(), player.getScore())));
        return stats;
    }
}
