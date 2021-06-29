package com.lulobank.clients.starter.v3.adapters.out.sqs.error;

import static com.lulobank.clients.starter.v3.adapters.out.sqs.error.SqsClientNotificationErrorMessage.ERROR_STATE_BLACKLIST;

public class SqsClientNotificationError extends Exception {
    SqsClientNotificationError(String message){
        super(message);
    }

    public static SqsClientNotificationError invalidBlacklistState(String message){
        return new SqsClientNotificationError(String.format(ERROR_STATE_BLACKLIST.getMessage(),message));
    }
}