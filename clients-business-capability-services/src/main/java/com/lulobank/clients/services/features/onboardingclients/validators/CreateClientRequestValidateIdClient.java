package com.lulobank.clients.services.features.onboardingclients.validators;

import com.lulobank.clients.services.exception.ValidateRequestException;
import com.lulobank.clients.services.features.onboardingclients.model.CreateClientRequest;
import com.lulobank.core.validations.ValidationResult;
import com.lulobank.core.validations.Validator;
import org.apache.commons.lang3.Validate;

public class CreateClientRequestValidateIdClient implements Validator<CreateClientRequest> {
  @Override
  public ValidationResult validate(CreateClientRequest createClientRequest) {
    try {
      Validate.notNull(createClientRequest);
      Validate.notNull(createClientRequest.getIdClient());
      Validate.notEmpty(createClientRequest.getIdClient());
      Validate.isTrue(
          createClientRequest
              .getIdClient()
              .matches(
                  "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[34][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}"));
    } catch (ValidateRequestException e) {
      return new ValidationResult("has a wrong format", "idClient");
    } catch (Exception e) {
      return new ValidationResult("has a wrong format", "idClient");
    }
    return null;
  }
}
