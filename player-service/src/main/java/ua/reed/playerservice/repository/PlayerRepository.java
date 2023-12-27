package ua.reed.playerservice.repository;

import ua.reed.playerservice.model.Player;

import java.util.List;

public interface PlayerRepository {

    Player findByName(final String name);

    List<Player> findAll();

    Player updateScore(final String name);

}
