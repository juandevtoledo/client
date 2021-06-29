package com.lulobank.clients.sdk.operations.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class ClientsFailureResult<T> implements ClientResult {
  @Getter
  private List<T> errors;

  public ClientsFailureResult(T... errors) {
    this.errors = Arrays.asList(errors);
  }
}
