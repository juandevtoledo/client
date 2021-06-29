package com.lulobank.clients.services.events;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventV2<T> {

    private String id;
    private String eventType;
    private T payload;
}
