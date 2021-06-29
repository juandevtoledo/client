package com.lulobank.clients.services.features.onboardingclients.model;

import java.io.Serializable;
import lombok.Getter;

@Getter
public class ValidationErrorCreateClientResponse implements Serializable {

  private static final long serialVersionUID = 1L;

  private String value;
  private String message;
  private TypeError typeError;

  public ValidationErrorCreateClientResponse(String value, String message, TypeError typeError) {
    this.message = message;
    this.value = value;
    this.typeError = typeError;
  }
}
