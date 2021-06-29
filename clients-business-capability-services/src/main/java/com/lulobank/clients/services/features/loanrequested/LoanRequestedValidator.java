package com.lulobank.clients.services.features.loanrequested;

import static com.lulobank.core.utils.ValidatorUtils.getNotNullValidations;

import com.lulobank.clients.sdk.operations.dto.ClientLoanRequested;
import com.lulobank.core.validations.ValidationResult;
import com.lulobank.core.validations.Validator;

public class LoanRequestedValidator implements Validator<ClientLoanRequested> {
  @Override
  public ValidationResult validate(ClientLoanRequested request) {
    return getNotNullValidations(request);
  }
}
