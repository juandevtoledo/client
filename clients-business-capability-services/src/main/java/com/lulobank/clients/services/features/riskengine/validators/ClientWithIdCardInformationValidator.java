package com.lulobank.clients.services.features.riskengine.validators;

import com.lulobank.clients.services.exception.ValidateRequestException;
import com.lulobank.clients.services.features.riskengine.model.ClientWithIdCardInformation;
import com.lulobank.core.validations.ValidationResult;
import com.lulobank.core.validations.Validator;
import java.util.Objects;
import org.apache.commons.lang3.Validate;

public class ClientWithIdCardInformationValidator
    implements Validator<ClientWithIdCardInformation> {

  public static final String HAS_A_WRONG_FORMAT = "has a wrong format";
  public static final String DATE_OF_ISSUE = "dateOfIssue";
  public static final String ID_CARD = "IdCard";
  public static final String REGEX_DATE = "^\\d{4}-\\d{2}-\\d{2}$";

  @Override
  public ValidationResult validate(ClientWithIdCardInformation clientWithIdCardInformation) {

    try {
      Validate.isTrue(clientWithIdCardInformation.getDateOfIssue().matches(REGEX_DATE));
    } catch (ValidateRequestException | IllegalArgumentException e) {
      return new ValidationResult(HAS_A_WRONG_FORMAT, DATE_OF_ISSUE);
    }
    if (Objects.isNull(clientWithIdCardInformation.getIdCard())) {
      return new ValidationResult(HAS_A_WRONG_FORMAT, ID_CARD);
    }
    if (Objects.isNull(clientWithIdCardInformation.getDateOfIssue())) {
      return new ValidationResult(HAS_A_WRONG_FORMAT, DATE_OF_ISSUE);
    }
    return null;
  }
}
