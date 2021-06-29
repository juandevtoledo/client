package com.lulobank.clients.starter.outboundadapter.sqs;

import com.lulobank.clientalerts.sdk.dto.event.notification.NewNotificationEvent;
import com.lulobank.clients.services.events.EventMapperV2;
import com.lulobank.clients.services.events.EventV2;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.utils.NotificationsUtils;
import com.lulobank.otp.sdk.dto.notifications.NotificationTypeEnum;

public class UpdateClientAddressNotificationEvent extends SqsIntegration<ClientEntity, NewNotificationEvent> {

  private static final String TITLE_CHANGE_ADDRESS_NOTIFICATION = "Dirección actualizada";
  private static final String DESCRIPTION_CHANGE_ADDRESS_NOTIFICATION =
          "Has actualizado tu dirección de contacto.";
  private static final String SUBJECT_CHANGE_ADDRESS_NOTIFICATION =
          "Cambio de dirección";

  public UpdateClientAddressNotificationEvent(String endpoint) {
    super(endpoint);
  }

  @Override
  public EventV2<NewNotificationEvent> map(ClientEntity event) {
    return EventMapperV2.of(NotificationsUtils.buildInAppNotification(
            event.getIdClient(),
            NotificationTypeEnum.SECURITY,
            TITLE_CHANGE_ADDRESS_NOTIFICATION,
            DESCRIPTION_CHANGE_ADDRESS_NOTIFICATION,
            SUBJECT_CHANGE_ADDRESS_NOTIFICATION,
            event.getEmailAddress(),
            event.getPhonePrefix().toString(),
            event.getPhoneNumber()));

  }
}
