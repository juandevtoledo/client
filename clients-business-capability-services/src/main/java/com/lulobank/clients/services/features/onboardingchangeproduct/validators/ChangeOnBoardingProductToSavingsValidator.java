package com.lulobank.clients.services.features.onboardingchangeproduct.validators;

import static com.lulobank.core.utils.ValidatorUtils.getNotNullValidations;

import com.lulobank.clients.sdk.operations.dto.onboarding.changeproduct.ChangeOnBoardingSelectedProductToSavingsForClient;
import com.lulobank.core.validations.ValidationResult;
import com.lulobank.core.validations.Validator;

public class ChangeOnBoardingProductToSavingsValidator
    implements Validator<ChangeOnBoardingSelectedProductToSavingsForClient> {

  @Override
  public ValidationResult validate(
      ChangeOnBoardingSelectedProductToSavingsForClient
          changeOnBoardingSelectedProductToSavingsForClient) {
    return getNotNullValidations(changeOnBoardingSelectedProductToSavingsForClient);
  }
}
