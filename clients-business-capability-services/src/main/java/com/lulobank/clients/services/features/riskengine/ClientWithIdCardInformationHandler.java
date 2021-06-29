package com.lulobank.clients.services.features.riskengine;

import static com.lulobank.clients.services.utils.ConverterObjectUtils.clientFromClientEntity;
import static com.lulobank.clients.services.utils.ConverterObjectUtils.getClientFromClientWithIdCardInformation;
import static com.lulobank.core.utils.ValidatorUtils.getListValidations;

import com.lulobank.clients.services.domain.Client;
import com.lulobank.clients.services.features.riskengine.model.ClientCreated;
import com.lulobank.clients.services.features.riskengine.model.ClientWithIdCardInformation;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.clients.services.utils.ClientErrorResultsEnum;
import com.lulobank.clients.services.utils.ConverterObjectUtils;
import com.lulobank.clients.services.utils.DatesUtil;
import com.lulobank.core.Handler;
import com.lulobank.core.Response;
import java.util.Optional;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public class ClientWithIdCardInformationHandler
    implements Handler<Response<ClientCreated>, ClientWithIdCardInformation> {
  private ClientsRepository clientsRepository;

  public ClientWithIdCardInformationHandler(ClientsRepository clientsRepository) {
    this.clientsRepository = clientsRepository;
  }

  @Override
  public Response<ClientCreated> handle(
      final ClientWithIdCardInformation clientWithIdCardInformation) {
    Optional<ClientEntity> clientEntityOptional =
        Optional.ofNullable(
            clientsRepository.findByIdCard(clientWithIdCardInformation.getIdCard()));
    Client client = getClientFrom(clientWithIdCardInformation, clientEntityOptional);
    if (Boolean.TRUE.equals(client.isConfirmedUser())) {
      return new Response<>(
          getListValidations(
              ClientErrorResultsEnum.CLIENT_CREATED.name(),
              ClientErrorResultsEnum.CLIENT_CREATED.name()));
    }
    saveClient(client);
    return new Response<>(new ClientCreated(client.getId()));
  }

  @NotNull
  private Client getClientFrom(
      ClientWithIdCardInformation clientWithIdCardInformation,
      Optional<ClientEntity> clientEntityOptional) {
    Client client;
    if (clientEntityOptional.isPresent()) {
      client = clientFromClientEntity(clientEntityOptional.get());
      client.setDateOfIssue(
          DatesUtil.convertStringYYYYmmDDToLocalDateTime(
              clientWithIdCardInformation.getDateOfIssue()));
    } else {
      client = getClientFromClientWithIdCardInformation(clientWithIdCardInformation);
      client.setId(UUID.randomUUID().toString());
    }
    return client;
  }

  private void saveClient(Client client) {
    clientsRepository.save(ConverterObjectUtils.clientEntityFromClient(client));
  }
}
