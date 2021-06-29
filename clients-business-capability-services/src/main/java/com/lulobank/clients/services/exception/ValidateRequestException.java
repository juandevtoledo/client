package com.lulobank.clients.services.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateRequestException extends RuntimeException {
  private String failure;
  private int code;

  public ValidateRequestException() {
    super();
  }

  public ValidateRequestException(String failure, int code) {
    super();
    this.failure = failure;
    this.code = code;
  }

}
