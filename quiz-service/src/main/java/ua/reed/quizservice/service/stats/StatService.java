package ua.reed.quizservice.service.stats;

import ua.reed.quizservice.model.GameStats;

import java.util.List;

public interface StatService {

    boolean isAnswerCorrect(final String answer);

    List<GameStats> getStats();

}
