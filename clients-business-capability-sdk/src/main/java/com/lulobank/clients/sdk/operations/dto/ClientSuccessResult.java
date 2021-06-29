package com.lulobank.clients.sdk.operations.dto;

public class ClientSuccessResult<T> implements ClientResult {
  private T content;

  public ClientSuccessResult(T content) {
    this.content = content;
  }

  public T getContent() {
    return content;
  }
}
