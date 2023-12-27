package ua.reed.playerservice.repository.impl;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import ua.reed.playerservice.model.Player;
import ua.reed.playerservice.repository.PlayerRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository
public class DefaultPlayerRepository implements PlayerRepository {

    private final Map<String, Player> playersMap = new HashMap<>();

    @PostConstruct
    public void init() {
        playersMap.put("Emma Thompson", new Player("Emma Thompson"));
        playersMap.put("Samuel Patel", new Player("Samuel Patel"));
        playersMap.put("Isabella Rodriguez", new Player("Isabella Rodriguez"));
        playersMap.put("Mason Davis", new Player("Mason Davis"));
    }

    @Override
    public Player findByName(final String name) {
        return Optional.ofNullable(playersMap.get(name))
                .orElseThrow(() -> new IllegalArgumentException("Player %s not found!".formatted(name)));
    }

    @Override
    public List<Player> findAll() {
        return new ArrayList<>(playersMap.values());
    }

    @Override
    public Player updateScore(final String name) {
        return playersMap.computeIfPresent(Objects.requireNonNull(name), (playerName, player) -> {
            player.addScore();
            return player;
        });
    }
}
