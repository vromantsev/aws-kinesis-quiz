package ua.reed.quizservice.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import ua.reed.quizservice.model.Question;

import java.util.List;

public final class JsonUtils {

    private JsonUtils() {}

    @SneakyThrows
    public static List<Question> createQuestionsFromJson(final ObjectMapper mapper, final String path) {
        return mapper.readValue(JsonUtils.class.getClassLoader().getResourceAsStream(path), new TypeReference<>() {});
    }
}
