package com.lulobank.clients.services.features.changepassword.actions;

import com.lulobank.clientalerts.sdk.dto.event.notification.NewNotificationEvent;
import com.lulobank.clients.services.features.changepassword.model.NewPasswordRequest;
import com.lulobank.clients.services.features.changepassword.model.Password;
import com.lulobank.clients.services.outboundadapters.SendMessageToSQS;
import com.lulobank.clients.services.utils.NotificationsUtils;
import com.lulobank.core.Response;
import com.lulobank.core.events.Event;
import com.lulobank.core.utils.EventUtils;
import com.lulobank.otp.sdk.dto.notifications.NotificationTypeEnum;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

public class MessageToSQSNotificationPasswordUpdated extends SendMessageToSQS<NewPasswordRequest> {

  private static final String TITLE_CHANGE_PASSWORD_NOTIFICATION = "Cambio de contraseña";
  private static final String DESCRIPTION_CHANGE_PASSWORD_NOTIFICATION =
      "Has cambiado la contraseña de acceso a la app.";
  private static final String SUBJECT_CHANGE_PASS_NOTIFICATION =
          "Cambio de contrase\u00f1a";


  public MessageToSQSNotificationPasswordUpdated(
      QueueMessagingTemplate queueMessagingTemplate, String sqsEndPoint) {
    super(queueMessagingTemplate, sqsEndPoint);
  }

  @Override
  public Event buildEvent(Response response, NewPasswordRequest command) {
    Password passwordRespone = (Password) response.getContent();
    return new EventUtils<NewNotificationEvent>()
        .getEvent(
            NotificationsUtils.buildInAppNotification(
                command.getIdClient(),
                NotificationTypeEnum.SECURITY,
                TITLE_CHANGE_PASSWORD_NOTIFICATION,
                DESCRIPTION_CHANGE_PASSWORD_NOTIFICATION,
                SUBJECT_CHANGE_PASS_NOTIFICATION,
                passwordRespone.getEmailAddress(),
                passwordRespone.getPhonePrefix(),
                passwordRespone.getPhoneNumber()));
  }
}
