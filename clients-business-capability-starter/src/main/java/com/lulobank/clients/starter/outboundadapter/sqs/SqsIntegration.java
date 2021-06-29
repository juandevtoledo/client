package com.lulobank.clients.starter.outboundadapter.sqs;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulobank.clients.services.events.EventV2;
import io.vavr.concurrent.Future;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
public abstract class SqsIntegration<T, R> {
    private String endpoint;

    public SqsIntegration(String endpoint) {
        this.endpoint = endpoint;
    }

    public abstract EventV2<R> map(T event);

    public void send(T event, Consumer<SqsCommand> consumer) {
        Future.run(() -> {
            EventV2<R> mappedEvent = map(event);
            ObjectMapper objectMapper = new ObjectMapper();
            consumer.accept(new SqsCommand(mappedEvent, endpoint));
            String jsonEvent = objectMapper.writeValueAsString(mappedEvent);
            log.info("Send event sqs reporting: {} , {}  to sqs {}, message json {}",
                    mappedEvent.getId(), mappedEvent.getEventType(), endpoint, jsonEvent);
        }).onFailure(ex -> log.error("Error sending event", ex));
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public class SqsCommand {

        private EventV2<R> event;
        private String endpoint;

    }

}
