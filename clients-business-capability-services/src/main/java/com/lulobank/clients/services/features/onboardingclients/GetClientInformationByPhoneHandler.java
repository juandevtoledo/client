package com.lulobank.clients.services.features.onboardingclients;

import com.lulobank.clients.services.features.onboardingclients.model.ClientInformationByPhone;
import com.lulobank.clients.services.features.onboardingclients.model.GetClientInformationByPhone;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.clients.services.utils.ClientErrorResultsEnum;
import com.lulobank.clients.services.utils.ConverterObjectUtils;
import com.lulobank.core.Handler;
import com.lulobank.core.Response;
import com.lulobank.core.validations.ValidationResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GetClientInformationByPhoneHandler
    implements Handler<Response<ClientInformationByPhone>, GetClientInformationByPhone> {

  private ClientsRepository repository;

  public GetClientInformationByPhoneHandler(ClientsRepository repository) {
    this.repository = repository;
  }

  @Override
  public Response<ClientInformationByPhone> handle(
      GetClientInformationByPhone clientTransactionRequest) {
    Optional<ClientEntity> clientEntity =
        repository.findByPhonePrefixAndPhoneNumber(
            clientTransactionRequest.getCountry(), clientTransactionRequest.getPhoneNumber());

    if (clientEntity.isPresent()) {
      return new Response<>(
          ConverterObjectUtils.createClientTransactionResponseFromClientEntity(clientEntity.get()));
    }

    return new Response<>(
        getValidationError(
            new ValidationResult(
                ClientErrorResultsEnum.CLIENT_NOT_FOUND_IN_DB.name(),
                ClientErrorResultsEnum.CLIENT_NOT_FOUND_IN_DB.name())));
  }

  private List<ValidationResult> getValidationError(ValidationResult clientNotFound) {
    List<ValidationResult> errors = new ArrayList<>();
    errors.add(clientNotFound);
    return errors;
  }
}
