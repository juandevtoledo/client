package com.lulobank.clients.sdk.operations.exception;

import lombok.Getter;

public class CreateCustomerServiceException extends RuntimeException {

    @Getter
    private final int code;

    public CreateCustomerServiceException(String message, int code) {
        super(message);
        this.code = code;
    }

}
