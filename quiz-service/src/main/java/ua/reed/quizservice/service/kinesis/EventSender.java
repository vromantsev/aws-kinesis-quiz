package ua.reed.quizservice.service.kinesis;

import ua.reed.quizservice.service.event.Event;

public interface EventSender {

    <E extends Event> void sendEvent(E event, String stream);

}
