package com.lulobank.clients.services.features.recoverpassword.validators;

import com.lulobank.clients.services.features.recoverpassword.model.ClientWithIdCard;
import com.lulobank.core.utils.ValidatorUtils;
import com.lulobank.core.validations.ValidationResult;
import com.lulobank.core.validations.Validator;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

public class ClientWithIdCardValidator implements Validator<ClientWithIdCard> {
  public static final String HAS_A_WRONG_FORMAT = "IdCard has a wrong format";

  @Override
  public ValidationResult validate(ClientWithIdCard clientWithIdCard) {
    ValidationResult validationResult = ValidatorUtils.getNotNullValidations(clientWithIdCard);

    if (Objects.isNull(validationResult)
        && StringUtils.EMPTY.equals(clientWithIdCard.getIdCard())) {
      validationResult =
          ValidatorUtils.getListValidations(HAS_A_WRONG_FORMAT, HttpStatus.BAD_REQUEST.name())
              .get(0);
    }
    return validationResult;
  }
}
