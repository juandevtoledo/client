package com.lulobank.clients.services.features.onboardingclients.validators;

import com.lulobank.clients.services.exception.ValidateRequestException;
import com.lulobank.clients.services.features.onboardingclients.model.CreateClientRequest;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.core.validations.ValidationResult;
import com.lulobank.core.validations.Validator;
import org.apache.commons.lang3.Validate;

public class CreateClientRequestValidateIdCard implements Validator<CreateClientRequest> {
  private static final String IDCARD = "idCard";
  private ClientsRepository repository;

  public CreateClientRequestValidateIdCard(ClientsRepository repository) {
    this.repository = repository;
  }

  @Override
  public ValidationResult validate(CreateClientRequest request) {
    try {
      Validate.notNull(request);
      Validate.notNull(request.getIdCard());
      Validate.notEmpty(request.getIdCard());
      Validate.isTrue(request.getIdCard().matches("[0-9]{7,11}"));
    } catch (NullPointerException | IllegalArgumentException e) {
      return new ValidationResult("has a wrong format", IDCARD);
    }
    try {
      clientExist(request);
    } catch (ValidateRequestException e) {
      return new ValidationResult("Not Exist in the database", IDCARD);
    }
    try {
      clientExitWithIdCardAndIdClient(request);
    } catch (ValidateRequestException e) {
      return new ValidationResult("Not belong to IdClient", IDCARD);
    }
    return null;
  }

  private void clientExist(CreateClientRequest clientRequest) {
    if (repository.findByIdCard(clientRequest.getIdCard()) == null) {
      throw new ValidateRequestException();
    }
  }

  private void clientExitWithIdCardAndIdClient(CreateClientRequest clientRequest) {
    if (!repository
        .findByIdClientAndIdCard(clientRequest.getIdClient(), clientRequest.getIdCard())
        .isPresent()) {
      throw new ValidateRequestException();
    }
  }
}
