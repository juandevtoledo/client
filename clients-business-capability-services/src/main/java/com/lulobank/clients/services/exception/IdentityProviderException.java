package com.lulobank.clients.services.exception;

public class IdentityProviderException extends RuntimeException {

    public IdentityProviderException(String message, Throwable e) {
        super(message, e);
    }

}
