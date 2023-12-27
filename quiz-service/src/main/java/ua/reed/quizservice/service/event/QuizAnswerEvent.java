package ua.reed.quizservice.service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizAnswerEvent implements Event {
    private String playerName;
    private String question;
    private String answer;
}
