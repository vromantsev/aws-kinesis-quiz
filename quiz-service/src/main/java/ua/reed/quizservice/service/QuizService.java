package ua.reed.quizservice.service;


import ua.reed.quizservice.dto.RestartQuizRequest;

public interface QuizService {

    void askQuestion();

    void consumeAnswer();

    void restart(final RestartQuizRequest request);

}
