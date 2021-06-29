package com.lulobank.clients.services.features.loanrequested;

import static com.lulobank.clients.services.features.loanrequested.GenerateOffer.getGenerateOffer;
import static com.lulobank.clients.services.utils.LogMessages.ERROR_GENERATING_OFFERS;

import com.lulobank.clients.sdk.operations.dto.ClientLoanRequested;
import com.lulobank.clients.services.outboundadapters.ClientsOutboundAdapter;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.core.Response;
import com.lulobank.core.actions.Action;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GenerateOfferAction implements Action<Response<ClientEntity>, ClientLoanRequested> {

  private ClientsOutboundAdapter clientsOutboundAdapter;

  public GenerateOfferAction(ClientsOutboundAdapter clientsOutboundAdapter) {
    this.clientsOutboundAdapter = clientsOutboundAdapter;
  }

  @Override
  public void run(
      Response<ClientEntity> clientEntityResponse, ClientLoanRequested clientLoanRequested) {
    ClientEntity clientEntity = clientEntityResponse.getContent();
    try {
      GenerateOffer generateOffer = getGenerateOffer(clientEntity, clientsOutboundAdapter);
      generateOffer.generate(clientEntity, clientLoanRequested);
    } catch (UnsupportedOperationException ex) {
      log.error(ERROR_GENERATING_OFFERS.getMessage(), ex.getMessage());
    }
  }
}
