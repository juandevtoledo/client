package com.lulobank.clients.services.features.recoverpassword.validators;

import static com.lulobank.core.utils.ValidatorUtils.getNotNullValidations;

import com.lulobank.clients.services.features.recoverpassword.model.RecoverPasswordUpdate;
import com.lulobank.core.validations.ValidationResult;
import com.lulobank.core.validations.Validator;
import java.util.Objects;
import org.apache.commons.lang3.Validate;
import org.springframework.http.HttpStatus;

public class RecoverPasswordUpdateValidator implements Validator<RecoverPasswordUpdate> {
  private static final String BAD_REQUEST = String.valueOf(HttpStatus.BAD_REQUEST.value());
  private static final String NOT_EMPTY = "cannot be empty";

  @Override
  public ValidationResult validate(RecoverPasswordUpdate recoverPasswordUpdate) {
    ValidationResult result = getNotNullValidations(recoverPasswordUpdate);
    if (Objects.nonNull(result)) {
      return result;
    }
    try {
      Validate.notEmpty(recoverPasswordUpdate.getEmailAddress(), "email" + NOT_EMPTY);
      Validate.notEmpty(recoverPasswordUpdate.getIdCard(), "idCard" + NOT_EMPTY);
      Validate.notEmpty(recoverPasswordUpdate.getNewPassword(), "newPassword" + NOT_EMPTY);
      Validate.notEmpty(
          recoverPasswordUpdate.getVerificationCode(), "verificationCode" + NOT_EMPTY);
    } catch (IllegalArgumentException e) {
      return new ValidationResult(e.getMessage(), BAD_REQUEST);
    }
    return null;
  }
}
