package com.lulobank.clients.services.features.reporting.validators;

import com.lulobank.clients.services.features.reporting.model.GenerateClientReportStatement;
import com.lulobank.clients.services.utils.ClientErrorResultsEnum;
import com.lulobank.core.validations.ValidationResult;
import com.lulobank.core.validations.Validator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.http.HttpStatus;

public class GenerateClientReportValidator implements Validator<GenerateClientReportStatement> {

  private static final String DATE_VALIDATOR_YYYY_MM_REGEX = "(20[1-9][0-9])[-](0[1-9]|1[0-2])";

  @Override
  public ValidationResult validate(GenerateClientReportStatement generateClientReportStatement) {

    if (StringUtils.isNotEmpty(generateClientReportStatement.getIdClient())
        && StringUtils.isNotEmpty(generateClientReportStatement.getIdProduct())) {
      try {
        Validate.isTrue(
            generateClientReportStatement.getInitialPeriod().matches(DATE_VALIDATOR_YYYY_MM_REGEX)
                && generateClientReportStatement
                    .getFinalPeriod()
                    .matches(DATE_VALIDATOR_YYYY_MM_REGEX));
      } catch (IllegalArgumentException e) {
        return new ValidationResult(
            ClientErrorResultsEnum.DATE_WRONG_FORMAT.name(),
            String.valueOf(HttpStatus.PRECONDITION_FAILED.value()));
      }
    } else {
      return new ValidationResult(
          ClientErrorResultsEnum.VALIDATION_ERROR.name(),
          String.valueOf(HttpStatus.PRECONDITION_FAILED.value()));
    }
    return null;
  }
}
