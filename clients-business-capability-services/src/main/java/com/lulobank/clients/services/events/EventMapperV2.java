package com.lulobank.clients.services.events;

import java.util.UUID;

public class EventMapperV2 {

    private EventMapperV2(){}

    public static <T> EventV2<T> of(T t){
        EventV2<T> event = new EventV2<>();
        event.setEventType(t.getClass().getSimpleName());
        event.setPayload(t);
        event.setId(UUID.randomUUID().toString());
        return event;
    }

}
