package ua.reed.playerservice.model;

import java.util.List;

public record Question(String question, List<String> options, String answer) {
}
