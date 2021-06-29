package com.lulobank.clients.starter.adapter.out.sqs;

import com.lulobank.clientalerts.sdk.dto.event.notification.NewNotificationEvent;
import com.lulobank.clients.v3.adapters.port.out.messaging.dto.NotificationDisabledTypeMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SqsMessagesMapper {

    SqsMessagesMapper INSTANCE = Mappers.getMapper(SqsMessagesMapper.class);

    @Mapping(constant = "NOTIFICATION_DISABLED", target = "transactionType")
    @Mapping(source = "operation", target = "description")
    @Mapping(source = "idClient", target = "inAppNotification.idClient")
    @Mapping(source = "title", target = "inAppNotification.tittle")
    @Mapping(source = "dateNotification", target = "inAppNotification.dateNotification")
    @Mapping(source = "description", target = "inAppNotification.description")
    @Mapping(constant = "NOTIFICATION_DISABLED", target = "inAppNotification.action")
    NewNotificationEvent toNewNotificationEvent(NotificationDisabledTypeMessage event);
}
