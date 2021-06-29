package com.lulobank.clients.services.features.onboardingclients.validators;

import com.lulobank.clients.services.exception.ValidateRequestException;
import com.lulobank.clients.services.features.onboardingclients.model.CreateClientRequest;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.core.validations.ValidationResult;
import com.lulobank.core.validations.Validator;
import org.apache.commons.lang3.Validate;

public class CreateClientRequestValidateEmail implements Validator<CreateClientRequest> {
  private ClientsRepository repository;

  public CreateClientRequestValidateEmail(ClientsRepository repository) {
    this.repository = repository;
  }

  @Override
  public ValidationResult validate(CreateClientRequest createClientRequest) {
    try {
      Validate.notNull(createClientRequest);
      Validate.notNull(createClientRequest.getEmail());
      Validate.notNull(createClientRequest.getEmail().getAddress());
      Validate.isTrue(
          createClientRequest
              .getEmail()
              .getAddress()
              .matches("^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$"));
      emailClientExist(createClientRequest);
    } catch (ValidateRequestException e) {
      return new ValidationResult("Exist in the database", "emailAddress");
    } catch (Exception e) {
      return new ValidationResult("has a wrong format", "emailAddress");
    }
    return null;
  }

  private void emailClientExist(CreateClientRequest clientRequest) {
    if (repository.findByEmailAddress(clientRequest.getEmail().getAddress()) != null) {
      throw new ValidateRequestException();
    }
  }
}
