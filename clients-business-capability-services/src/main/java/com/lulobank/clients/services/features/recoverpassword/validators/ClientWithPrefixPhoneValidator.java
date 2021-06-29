package com.lulobank.clients.services.features.recoverpassword.validators;

import com.lulobank.clients.services.features.recoverpassword.model.RecoverPassword;
import com.lulobank.clients.services.utils.ClientErrorResultsEnum;
import com.lulobank.clients.services.utils.UtilValidators;
import com.lulobank.core.validations.ValidationResult;
import com.lulobank.core.validations.Validator;
import org.springframework.http.HttpStatus;

public class ClientWithPrefixPhoneValidator implements Validator<RecoverPassword> {
  @Override
  public ValidationResult validate(RecoverPassword recoverPassword) {
    try {
      UtilValidators.validatePrefix(String.valueOf(recoverPassword.getPrefix()));
    } catch (Exception e) {
      return new ValidationResult(
          ClientErrorResultsEnum.CLIENT_WRONG_PREFIX_FORMAT.name(),
          String.valueOf(HttpStatus.BAD_REQUEST.value()));
    }
    return null;
  }
}
