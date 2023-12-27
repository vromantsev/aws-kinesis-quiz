package ua.reed.quizservice.service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ua.reed.quizservice.model.GameStats;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatsEvent implements Event {
    private List<GameStats> stats;
}
