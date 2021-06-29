package com.lulobank.clients.services.features.profile.action;

import com.lulobank.clientalerts.sdk.dto.event.notification.NewNotificationEvent;
import com.lulobank.clients.services.features.profile.model.UpdateEmailClientRequest;
import com.lulobank.clients.services.features.profile.model.UpdateEmailClientResponse;
import com.lulobank.clients.services.outboundadapters.SendMessageToSQS;
import com.lulobank.clients.services.utils.NotificationsUtils;
import com.lulobank.core.Response;
import com.lulobank.core.events.Event;
import com.lulobank.core.utils.EventUtils;
import com.lulobank.otp.sdk.dto.notifications.NotificationTypeEnum;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

public class MessageToSQSNotificationEmailUpdated
    extends SendMessageToSQS<UpdateEmailClientRequest> {

  private static final String TITLE_CHANGE_EMAIL_NOTIFICATION = "Actualizaste tu correo";
  private static final String DESCRIPTION_CHANGE_EMAIL_NOTIFICATION =
      "Has actualizado tu correo electrónico.";
  private static final String SUBJECT_CHANGE_EMAIL_NOTIFICATION = "Cambio de correo";

  public MessageToSQSNotificationEmailUpdated(
      QueueMessagingTemplate queueMessagingTemplate, String sqsEndPoint) {
    super(queueMessagingTemplate, sqsEndPoint);
  }

  @Override
  public Event buildEvent(Response response, UpdateEmailClientRequest command) {
    UpdateEmailClientResponse updateEmailClientResponse =
        (UpdateEmailClientResponse) response.getContent();
    return new EventUtils<NewNotificationEvent>()
        .getEvent(
            NotificationsUtils.buildInAppNotification(
                command.getIdClient(),
                NotificationTypeEnum.SECURITY,
                TITLE_CHANGE_EMAIL_NOTIFICATION,
                DESCRIPTION_CHANGE_EMAIL_NOTIFICATION,
                    SUBJECT_CHANGE_EMAIL_NOTIFICATION,
                updateEmailClientResponse.getEmailAddress(),
                updateEmailClientResponse.getPhonePrefix(),
                updateEmailClientResponse.getPhoneNumber()));
  }
}
