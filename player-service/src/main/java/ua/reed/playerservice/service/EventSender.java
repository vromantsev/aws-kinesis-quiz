package ua.reed.playerservice.service;


import ua.reed.playerservice.service.event.Event;

public interface EventSender {

    <E extends Event> void sendEvent(E event, String stream);

}
