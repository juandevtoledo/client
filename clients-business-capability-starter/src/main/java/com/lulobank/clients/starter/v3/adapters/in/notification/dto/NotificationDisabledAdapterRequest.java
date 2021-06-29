package com.lulobank.clients.starter.v3.adapters.in.notification.dto;

import com.lulobank.clients.v3.usecase.notification.NotificationDisabledType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class NotificationDisabledAdapterRequest {
    @NotNull(message = "notificationType is null or empty")
    private NotificationDisabledType notificationType;
}
