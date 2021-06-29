package com.lulobank.clients.services.utils;

import com.lulobank.clientalerts.sdk.dto.event.notification.InAppNotification;
import com.lulobank.clientalerts.sdk.dto.event.notification.NewNotificationEvent;
import com.lulobank.otp.sdk.dto.notifications.NotificationTypeEnum;

public class NotificationsUtils {

  public static NewNotificationEvent buildInAppNotification(
      String idClient,
      NotificationTypeEnum notificationTypeEnum,
      String tittle,
      String description,
      String subject,
      String email,
      String phonePrefix,
      String phoneNumber) {
    NewNotificationEvent newNotificationEvent = new NewNotificationEvent();
    newNotificationEvent.setTransactionType(notificationTypeEnum.toString());
    newNotificationEvent.setEmailSubject(subject);
    InAppNotification inAppNotification = new InAppNotification();
    inAppNotification.setDateNotification(DatesUtil.getLocalDateGMT5().toString());
    inAppNotification.setIdClient(idClient);
    inAppNotification.setTittle(tittle);
    inAppNotification.setDescription(description);
    inAppNotification.setEmail(email);
    inAppNotification.setPhonePrefix(phonePrefix);
    inAppNotification.setPhoneNumber(phoneNumber);
    inAppNotification.setDescription(description);
    newNotificationEvent.setInAppNotification(inAppNotification);
    return newNotificationEvent;
  }
}
