package com.lulobank.clients.sdk.operations.dto;

import com.lulobank.core.validations.ValidationResult;
import java.util.List;

public class ClientErrorResult implements ClientResult {
  private List<ValidationResult> errors;

  public ClientErrorResult(List<ValidationResult> errors) {
    this.errors = errors;
  }

  public List<ValidationResult> getErrors() {
    return this.errors;
  }
}
