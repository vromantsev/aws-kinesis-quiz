package ua.reed.quizservice.service;

import ua.reed.quizservice.service.event.Event;

public interface EventSender {

    <E extends Event> void sendEvent(E event, String stream);

}
