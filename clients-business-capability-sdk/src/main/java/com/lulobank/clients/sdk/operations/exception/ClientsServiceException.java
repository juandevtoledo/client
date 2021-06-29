package com.lulobank.clients.sdk.operations.exception;

import lombok.Getter;

public class ClientsServiceException extends RuntimeException {

    @Getter
    private final int code;

    public ClientsServiceException(String message, Throwable t) {
        super(message, t);
        code = 0;
    }

    public ClientsServiceException(String message, int code) {
        super(message);
        this.code = code;
    }

}
