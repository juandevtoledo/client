package com.lulobank.clients.services.domain.notification;

import com.lulobank.clients.v3.usecase.notification.NotificationDisabledType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationDisabledRequest {
    private String idClient;
    private NotificationDisabledType notificationType;
}
