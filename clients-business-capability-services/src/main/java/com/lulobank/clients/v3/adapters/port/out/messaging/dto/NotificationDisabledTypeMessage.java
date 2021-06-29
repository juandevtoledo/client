package com.lulobank.clients.v3.adapters.port.out.messaging.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationDisabledTypeMessage {
    private String idClient;
    private String operation;
    private NotificationCategory category;
    private String title;
    private String description;
    private String dateNotification;
}
