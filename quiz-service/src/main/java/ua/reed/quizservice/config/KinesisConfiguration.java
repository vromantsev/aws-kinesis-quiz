package ua.reed.quizservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.KinesisClient;

import java.net.URI;

@Configuration
public class KinesisConfiguration {

    @Profile("local")
    @Autowired
    @Bean
    public KinesisClient localKinesisClient(final AwsCredentialsProvider credentialsProvider,
                                            final KinesisProperties kinesisProperties) {
        return KinesisClient.builder()
                .credentialsProvider(credentialsProvider)
                .region(Region.EU_NORTH_1)
                .endpointOverride(URI.create(kinesisProperties.getUrl()))
                .build();
    }

    @Profile("local")
    @Bean
    public AwsCredentialsProvider localCredentialsProvider() {
        return ProfileCredentialsProvider.create();
    }

    @Profile("aws")
    @Autowired
    @Bean
    public KinesisClient awsKinesisClient(final AwsCredentialsProvider credentialsProvider) {
        return KinesisClient.builder()
                .credentialsProvider(credentialsProvider)
                .region(Region.EU_NORTH_1)
                .build();
    }

    @Profile("aws")
    @Bean
    public AwsCredentialsProvider awsCredentialsProvider() {
        return ProfileCredentialsProvider.create();
    }
}
