package ua.reed.quizservice.service.event;

import java.util.UUID;

public interface Event {

    default UUID getPartitionKey() {
        return UUID.randomUUID();
    }
}
