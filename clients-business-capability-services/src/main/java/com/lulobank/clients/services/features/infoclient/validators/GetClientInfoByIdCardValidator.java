package com.lulobank.clients.services.features.infoclient.validators;

import com.lulobank.clients.sdk.operations.dto.GetClientInformationByIdCard;
import com.lulobank.clients.services.utils.ClientErrorResultsEnum;
import com.lulobank.core.validations.ValidationResult;
import com.lulobank.core.validations.Validator;
import org.apache.commons.lang3.Validate;
import org.springframework.http.HttpStatus;

public class GetClientInfoByIdCardValidator implements Validator<GetClientInformationByIdCard> {

  @Override
  public ValidationResult validate(GetClientInformationByIdCard getClientInformationByIdCard) {
    try {
      Validate.notNull(getClientInformationByIdCard);
      Validate.notEmpty(getClientInformationByIdCard.getIdCard());
    } catch (Exception e) {
      return new ValidationResult(
          ClientErrorResultsEnum.VALIDATION_ERROR.name(),
          String.valueOf(HttpStatus.BAD_REQUEST.value()));
    }
    return null;
  }
}
