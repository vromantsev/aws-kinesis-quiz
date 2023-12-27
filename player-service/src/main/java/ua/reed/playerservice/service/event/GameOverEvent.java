package ua.reed.playerservice.service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ua.reed.playerservice.model.GameStats;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameOverEvent implements Event {
    private String message;
    private List<GameStats> stats;
}
