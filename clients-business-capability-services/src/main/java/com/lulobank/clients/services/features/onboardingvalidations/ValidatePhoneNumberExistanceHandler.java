package com.lulobank.clients.services.features.onboardingvalidations;

import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.core.Handler;
import com.lulobank.core.Response;
import java.util.Optional;

public class ValidatePhoneNumberExistanceHandler
    implements Handler<Response<ValidateExistenceResult>, ValidatePhoneNumberExistance> {

  private ClientsRepository repository;

  public ValidatePhoneNumberExistanceHandler(ClientsRepository repository) {
    this.repository = repository;
  }

  @Override
  public Response<ValidateExistenceResult> handle(ValidatePhoneNumberExistance command) {
    Optional<ClientEntity> clientEntity =
        repository.findByPhonePrefixAndPhoneNumber(command.getCountry(), command.getNumber());
    return new Response<>(new ValidateExistenceResult(clientEntity.isPresent()));
  }
}
