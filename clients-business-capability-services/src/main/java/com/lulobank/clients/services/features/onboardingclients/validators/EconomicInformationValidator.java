package com.lulobank.clients.services.features.onboardingclients.validators;

import static com.lulobank.core.utils.ValidatorUtils.getNotNullValidations;

import com.lulobank.clients.sdk.operations.dto.economicinformation.ClientEconomicInformation;
import com.lulobank.core.validations.ValidationResult;
import com.lulobank.core.validations.Validator;

public class EconomicInformationValidator implements Validator<ClientEconomicInformation> {
  @Override
  public ValidationResult validate(ClientEconomicInformation request) {
    return getNotNullValidations(request);
  }
}
