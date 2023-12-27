package ua.reed.playerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PlayerServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PlayerServiceApplication.class, args);
    }
}
