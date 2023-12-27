package ua.reed.quizservice.repository;

import ua.reed.quizservice.model.Question;

import java.util.List;

public interface QuestionRepository {

    List<Question> getQuestions();

    boolean isAsked(final Question question);

    void updateAskedQuestions(final Question question);

    void clearAskedQuestions();

}
