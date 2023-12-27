package ua.reed.quizservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "kinesis")
public class KinesisProperties {
    private String questionStream;
    private String answersStream;
    private String statsStream;
    private String url;
}
