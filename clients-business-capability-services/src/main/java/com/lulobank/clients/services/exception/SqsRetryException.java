package com.lulobank.clients.services.exception;

public class SqsRetryException extends RuntimeException {

  private int code;

  public SqsRetryException(String message) {
    super(message);
    this.code = 500;
  }

  public int getCode() {
    return this.code;
  }
}
