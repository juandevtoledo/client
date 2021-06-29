package com.lulobank.clients.services.features.onboardingclients;

import com.lulobank.clients.services.domain.Client;
import com.lulobank.clients.services.features.onboardingclients.model.CreateClientRequest;
import com.lulobank.clients.services.features.onboardingclients.model.CreateClientResponse;
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

public class ClientHandler implements Handler<Response<CreateClientResponse>, CreateClientRequest> {

  private ClientsRepository clientsRepository;

  public ClientHandler(ClientsRepository clientsRepository) {
    this.clientsRepository = clientsRepository;
  }

  @Override
  public Response<CreateClientResponse> handle(CreateClientRequest createClientRequest) {
    Optional<ClientEntity> clientEntity =
        clientsRepository.findByIdClient(createClientRequest.getIdClient());
    if (clientEntity.isPresent()) {
      Client client = ConverterObjectUtils.getClientFromCreateClientRequest(createClientRequest);
      ClientEntity clientEntityRequest = ConverterObjectUtils.initClientEntityFromClient(client);
      clientEntityRequest.setIdClient(clientEntity.get().getIdClient());
      clientEntityRequest.setDateOfIssue(clientEntity.get().getDateOfIssue());
      clientsRepository.save(clientEntityRequest);
      return new Response<>(
          new CreateClientResponse(String.valueOf(clientEntityRequest.getIdClient())));
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
