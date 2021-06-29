package com.lulobank.clients.services.features.onboardingclients.validators;

import com.lulobank.clients.services.exception.ValidateRequestException;
import com.lulobank.clients.services.features.onboardingclients.model.CreateClientRequest;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.core.validations.ValidationResult;
import com.lulobank.core.validations.Validator;
import org.apache.commons.lang3.Validate;

public class CreateClientRequestValidatePhone implements Validator<CreateClientRequest> {
  private ClientsRepository repository;

  public CreateClientRequestValidatePhone(ClientsRepository repository) {
    this.repository = repository;
  }

  @Override
  public ValidationResult validate(CreateClientRequest createClientRequest) {
    try {
      Validate.notNull(createClientRequest);
      Validate.notNull(createClientRequest.getPhone());
      Validate.notNull(createClientRequest.getPhone().getNumber());
      Validate.isTrue(createClientRequest.getPhone().getNumber().matches("[0-9]{10}"));
      phoneClientExist(createClientRequest);
    } catch (ValidateRequestException e) {
      return new ValidationResult("Exist in the database", "phoneNumber");
    } catch (Exception e) {
      return new ValidationResult("has a wrong format", "phoneNumber");
    }
    return null;
  }

  private void phoneClientExist(CreateClientRequest clientRequest) {
    if (repository
        .findByPhonePrefixAndPhoneNumber(
            clientRequest.getPhone().getPrefix(), clientRequest.getPhone().getNumber())
        .isPresent()) {
      throw new ValidateRequestException();
    }
  }
}
