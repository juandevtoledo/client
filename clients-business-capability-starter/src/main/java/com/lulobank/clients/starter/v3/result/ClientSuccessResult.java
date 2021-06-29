package com.lulobank.clients.starter.v3.result;

public class ClientSuccessResult<T> implements ClientResult {
    private final T content;

    public ClientSuccessResult(T content) {
        this.content = content;
    }

    public T getContent() {
        return content;
    }
}
