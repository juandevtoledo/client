package com.lulobank.clients.services.features.onboardingclients;

import static com.lulobank.clients.services.utils.ClientErrorResultsEnum.CLIENT_NOT_FOUND_IN_DB;

import com.lulobank.clients.services.events.CreditAccepted;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.utils.exception.ServiceException;

public class UpdateCreditAcceptHandler extends UpdateCheckPointClientHandler<CreditAccepted> {

  public UpdateCreditAcceptHandler(ClientsRepository clientsRepository) {
    super(clientsRepository);
  }

  @Override
  ClientEntity getClientEntity(CreditAccepted checkPointClient) {
    return getClientsRepository()
        .findByIdClientAndOnBoardingStatusNotNull(checkPointClient.getIdClient())
        .orElseThrow(() -> new ServiceException(CLIENT_NOT_FOUND_IN_DB.name()));
  }
}
