package com.lulobank.clients.services.exception;

public class CoreBankingException extends RuntimeException {

    public CoreBankingException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
