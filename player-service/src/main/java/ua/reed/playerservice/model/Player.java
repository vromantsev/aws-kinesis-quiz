package ua.reed.playerservice.model;

import java.util.Objects;

public final class Player {

    private static final int MAX_SCORE = 1000;
    private static final int CORRECT_ANSWER_SCORE_POINTS = 100;

    private String name;
    private int score;

    public Player(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean hasMaxScore() {
        return score >= MAX_SCORE;
    }

    public void addScore() {
        score += CORRECT_ANSWER_SCORE_POINTS;
    }

    public String userStats() {
        return "Player %s scored %d points".formatted(name, score);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Player) obj;
        return Objects.equals(this.name, that.name) &&
                this.score == that.score;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, score);
    }
}
