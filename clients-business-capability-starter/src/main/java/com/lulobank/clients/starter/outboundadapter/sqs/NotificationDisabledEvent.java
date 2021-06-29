package com.lulobank.clients.starter.outboundadapter.sqs;

import com.lulobank.clientalerts.sdk.dto.event.notification.NewNotificationEvent;
import com.lulobank.clients.services.events.EventMapperV2;
import com.lulobank.clients.services.events.EventV2;
import com.lulobank.clients.starter.adapter.out.sqs.SqsMessagesMapper;
import com.lulobank.clients.v3.adapters.port.out.messaging.dto.NotificationDisabledTypeMessage;

public class NotificationDisabledEvent extends SqsIntegration<NotificationDisabledTypeMessage, NewNotificationEvent>{


    public NotificationDisabledEvent(String endpoint) {
        super(endpoint);
    }

    @Override
    public EventV2<NewNotificationEvent> map(NotificationDisabledTypeMessage event) {
        NewNotificationEvent newNotificationEvent =
                SqsMessagesMapper.INSTANCE.toNewNotificationEvent(event);
        return EventMapperV2.of(newNotificationEvent);
    }
}
