package ua.reed.quizservice.repository;

import ua.reed.quizservice.model.Player;

import java.util.List;

public interface PlayerRepository {

    Player findByName(final String name);

    List<Player> findAll();

    Player updateScore(final String name);

}
