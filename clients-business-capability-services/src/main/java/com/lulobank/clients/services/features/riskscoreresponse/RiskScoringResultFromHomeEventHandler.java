package com.lulobank.clients.services.features.riskscoreresponse;

import static com.lulobank.clients.services.utils.ClientHelper.ID_CLIENT_KEY;
import static com.lulobank.clients.services.utils.ClientHelper.LOANREQUESTED_VERIFICATION;
import static com.lulobank.clients.services.utils.ClientHelper.getFirebaseFailParamsFromHomeCredit;
import static com.lulobank.clients.services.utils.ClientHelper.notifyLoanFromHomeCreated;
import static com.lulobank.clients.services.utils.ClientHelper.notifyLoanFromHomeFailed;

import com.lulobank.clients.services.outboundadapters.ClientsOutboundAdapter;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.model.LoanClientRequested;
import com.lulobank.clients.services.utils.LoanRequestedStatus;
import com.lulobank.clients.services.utils.LoanRequestedStatusFirebase;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class RiskScoringResultFromHomeEventHandler extends RiskScoringResultEvent {

  public RiskScoringResultFromHomeEventHandler(ClientsOutboundAdapter clientsOutboundAdapter) {
    super(clientsOutboundAdapter);
  }

  @Override
  LoanClientRequested getLoanRequested(ClientEntity clientEntity) {
    return clientEntity.getLoanRequested().getLoanClientRequested();
  }

  private Predicate<ClientEntity> isLoanRequestedNotFinished =
      clientEntity ->
          !LoanRequestedStatus.FINISHED.name().equals(clientEntity.getLoanRequested().getStatus());

  @Override
  void notifyLoanFinished(
      ClientsOutboundAdapter clientsOutboundAdapter,
      ClientEntity clientEntity,
      Map<String, Object> users) {
    clientEntity.getLoanRequested().setStatus(LoanRequestedStatus.FINISHED.name());
    notifyLoanFromHomeCreated.accept(clientsOutboundAdapter, users);
  }

  @Override
  Map<String, Object> getFirebaseFailParams(ClientEntity clientEntity, String detail) {
    return getFirebaseFailParamsFromHomeCredit(clientEntity, detail);
  }

  @Override
  void notifyLoanFailed(ClientsOutboundAdapter clientsOutboundAdapter, Map<String, Object> users) {
    notifyLoanFromHomeFailed.accept(clientsOutboundAdapter, users);
  }

  @Override
  boolean isLoanRequestedNotFinished(ClientEntity clientEntity) {
    return isLoanRequestedNotFinished.test(clientEntity);
  }

  @Override
  Map<String, Object> getFirebaseNotification(ClientEntity clientEntity) {
    Map<String, Object> users = new HashMap<>();
    users.put(
        LOANREQUESTED_VERIFICATION,
        new LoanRequestedStatusFirebase(LoanRequestedStatus.FINISHED.name()));
    users.put(ID_CLIENT_KEY, clientEntity.getIdClient());
    return users;
  }
}
