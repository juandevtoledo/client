package com.lulobank.clients.services.features.identitybiometric.validators;

import static com.lulobank.core.utils.ValidatorUtils.getNotNullValidations;

import com.lulobank.clients.services.features.identitybiometric.model.UpdateIdTransactionBiometric;
import com.lulobank.core.validations.ValidationResult;
import com.lulobank.core.validations.Validator;

public class UpdateIdTransactionBiometricValidator
    implements Validator<UpdateIdTransactionBiometric> {
  @Override
  public ValidationResult validate(UpdateIdTransactionBiometric updateIdTransactionBiometric) {
    return getNotNullValidations(updateIdTransactionBiometric);
  }
}
