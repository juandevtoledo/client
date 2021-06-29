package com.lulobank.clients.services.features.loanrequested;

import static com.lulobank.clients.services.utils.ClientHelper.ID_CLIENT_KEY;
import static com.lulobank.clients.services.utils.ClientHelper.LOANREQUESTED_VERIFICATION;
import static com.lulobank.clients.services.utils.ClientHelper.isAmountPresent;
import static com.lulobank.clients.services.utils.ClientHelper.isClientRiskAnalisisPresent;
import static com.lulobank.clients.services.utils.ClientHelper.notifyLoanFromHomeCreated;
import static com.lulobank.clients.services.utils.ClientHelper.notifyLoanFromHomeFailed;
import static com.lulobank.clients.services.utils.LoanRequestedStatus.FINISHED;

import com.lulobank.clients.services.outboundadapters.ClientsOutboundAdapter;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.utils.LoanRequestedStatusFirebase;
import java.util.HashMap;
import java.util.Map;

public class GenerateOfferFromHome extends GenerateOffer {
  private ClientsOutboundAdapter clientsOutboundAdapter;

  public GenerateOfferFromHome(ClientsOutboundAdapter clientsOutboundAdapter) {
    super(clientsOutboundAdapter);
    this.clientsOutboundAdapter = clientsOutboundAdapter;
  }

  @Override
  void notifyLoanFinished(ClientEntity clientEntity) {
    Map<String, Object> users = getFirebaseMessages(clientEntity);
    notifyLoanFromHomeCreated.accept(clientsOutboundAdapter, users);
  }

  @Override
  void notifyLoanFailed(ClientEntity clientEntity) {
    Map<String, Object> users = getFirebaseMessages(clientEntity);
    notifyLoanFromHomeFailed.accept(clientsOutboundAdapter, users);
  }

  @Override
  Map<String, Object> getFirebaseMessages(ClientEntity clientEntity) {
    clientEntity.getLoanRequested().setStatus(FINISHED.name());
    Map<String, Object> users = new HashMap<>();
    users.put(ID_CLIENT_KEY, clientEntity.getIdClient());
    users.put(
        LOANREQUESTED_VERIFICATION,
        new LoanRequestedStatusFirebase(clientEntity.getLoanRequested().getStatus()));
    return users;
  }

  @Override
  boolean validateUpdateOffers(ClientEntity clientEntity) {
    return isAmountPresent.and(isClientRiskAnalisisPresent).test(clientEntity);
  }
}
