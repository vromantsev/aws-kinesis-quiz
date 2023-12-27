package ua.reed.playerservice.service.event;

import java.util.UUID;

public interface Event {

    default UUID getPartitionKey() {
        return UUID.randomUUID();
    }
}
