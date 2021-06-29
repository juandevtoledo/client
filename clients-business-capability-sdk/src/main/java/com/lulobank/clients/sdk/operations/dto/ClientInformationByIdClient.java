package com.lulobank.clients.sdk.operations.dto;

import com.lulobank.core.validations.ValidationResult;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientInformationByIdClient {

  private ClientInformationByIdClientContent content;
  List<ValidationResult> errors;
  private Boolean hasErrors;
}
