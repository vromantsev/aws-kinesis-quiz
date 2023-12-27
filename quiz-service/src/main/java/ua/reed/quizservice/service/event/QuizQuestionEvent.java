package ua.reed.quizservice.service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestionEvent implements Event {
    private String question;
    private List<String> options;
}
