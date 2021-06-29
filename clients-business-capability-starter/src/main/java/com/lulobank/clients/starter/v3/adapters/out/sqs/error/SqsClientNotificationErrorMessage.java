package com.lulobank.clients.starter.v3.adapters.out.sqs.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SqsClientNotificationErrorMessage {

    ERROR_STATE_BLACKLIST("Invalid blacklist state: %s."),
    ;

    private final String message;
}