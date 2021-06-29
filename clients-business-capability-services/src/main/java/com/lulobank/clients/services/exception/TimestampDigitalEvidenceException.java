package com.lulobank.clients.services.exception;

public class TimestampDigitalEvidenceException extends RuntimeException {

    public TimestampDigitalEvidenceException(String message, Throwable e) {
        super(message, e);
    }

    public TimestampDigitalEvidenceException(String message) {
        super(message);
    }
}
