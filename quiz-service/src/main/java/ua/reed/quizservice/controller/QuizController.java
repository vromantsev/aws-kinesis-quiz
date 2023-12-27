package ua.reed.quizservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ua.reed.quizservice.dto.RestartQuizRequest;
import ua.reed.quizservice.service.QuizService;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/ask")
    public void askQuestion() {
        this.quizService.askQuestion();
    }

    @PostMapping("/restart")
    @ResponseStatus(HttpStatus.OK)
    public void restartQuiz(@RequestBody final RestartQuizRequest request) {
        this.quizService.restart(request);
    }
}
