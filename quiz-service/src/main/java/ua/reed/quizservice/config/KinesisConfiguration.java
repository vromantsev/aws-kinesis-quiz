package ua.reed.quizservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.KinesisClient;

import java.net.URI;

@Configuration
public class KinesisConfiguration {

    @Autowired
    @Bean
    public KinesisClient kinesisClient(final AwsCredentialsProvider credentialsProvider,
                                       final KinesisProperties kinesisProperties) {
        return KinesisClient.builder()
                .credentialsProvider(credentialsProvider)
                .region(Region.EU_NORTH_1)
                .endpointOverride(URI.create(kinesisProperties.getUrl()))
                .build();
    }

    @Bean
    public AwsCredentialsProvider credentialsProvider() {
        return ProfileCredentialsProvider.create();
    }
}
