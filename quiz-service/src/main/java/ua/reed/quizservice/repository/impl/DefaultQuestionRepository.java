package ua.reed.quizservice.repository.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ua.reed.quizservice.model.Question;
import ua.reed.quizservice.repository.QuestionRepository;
import ua.reed.quizservice.utils.JsonUtils;

import java.util.ArrayList;
import java.util.List;


@Repository
@RequiredArgsConstructor
public class DefaultQuestionRepository implements QuestionRepository {

    private static final String QUIZ_JSON_FILE_PATH = "data/quiz.json";

    private final ObjectMapper objectMapper;
    private List<Question> questions;
    private List<Question> askedQuestions = new ArrayList<>();

    @PostConstruct
    public void init() {
        this.questions = JsonUtils.createQuestionsFromJson(objectMapper, QUIZ_JSON_FILE_PATH);
    }

    @Override
    public List<Question> getQuestions() {
        return questions.stream()
                .filter(q -> !isAsked(q))
                .toList();
    }

    @Override
    public boolean isAsked(final Question question) {
        return this.askedQuestions.contains(question);
    }

    @Override
    public void updateAskedQuestions(final Question question) {
        this.askedQuestions.add(question);
    }

    @Override
    public void clearAskedQuestions() {
        this.askedQuestions.clear();
    }
}
