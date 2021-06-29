package com.lulobank.clients.services.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulobank.core.events.Event;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventUtils<T> {
    // TO DO: Quitar esta clase, cuando este codigo este en el core
    public static Event getEvent(String json, String packageName) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return Try.of(() -> mapper.readValue(json, Event.class))
                .mapTry(prev -> mapper
                        .getTypeFactory()
                        .constructParametricType(
                                Event.class, Class.forName(packageName + ".events." + prev.getEventType())))
                .mapTry(eventType -> mapper.readValue(json, eventType))
                .map(event -> (Event) event)
                .onFailure(error -> log.error(String.format("error getting event from json, msg : %s ", error.getMessage()), error))
                .getOrNull();
    }
}