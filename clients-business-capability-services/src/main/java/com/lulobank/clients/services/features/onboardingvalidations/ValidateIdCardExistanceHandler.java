package com.lulobank.clients.services.features.onboardingvalidations;

import com.lulobank.clients.services.domain.Client;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.clients.services.utils.ConverterObjectUtils;
import com.lulobank.core.Handler;
import com.lulobank.core.Response;
import java.util.Optional;

public class ValidateIdCardExistanceHandler
    implements Handler<Response<ValidateExistenceResult>, ValidateExistenceRequest> {

  private ClientsRepository repository;

  public ValidateIdCardExistanceHandler(ClientsRepository repository) {
    this.repository = repository;
  }

  @Override
  public Response<ValidateExistenceResult> handle(
      ValidateExistenceRequest validateExistenceRequest) {
    Optional<ClientEntity> clientEntity =
        Optional.ofNullable(repository.findByIdCard(validateExistenceRequest.getPropertie()));
    Boolean exists = false;
    if (clientEntity.isPresent()) {
      Client client = ConverterObjectUtils.clientFromClientEntity(clientEntity.get());
      exists = client.isConfirmedUser();
    }
    return new Response<>(new ValidateExistenceResult(exists));
  }
}
