package com.lulobank.clients.services.exception;

public class ClientsNotificationException extends RuntimeException {

    public ClientsNotificationException(String message, Throwable e) {
        super(message, e);
    }
}
