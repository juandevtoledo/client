package com.lulobank.clients.services.features.riskscoreresponse;

import static com.lulobank.clients.services.features.onboardingclients.OnboardingClientsConverter.createClientVerificationFirebaseOKFromEntity;
import static com.lulobank.clients.services.utils.ClientHelper.ID_CLIENT_KEY;
import static com.lulobank.clients.services.utils.ClientHelper.getFirebaseFailParamsOnboardingCredit;
import static com.lulobank.clients.services.utils.ClientHelper.notifyLoanOnBoardingFailed;
import static com.lulobank.clients.services.utils.ClientHelper.notifyLoanOnBordingCreated;
import static com.lulobank.core.utils.OnboardingStatus.FINISH_ON_BOARDING;

import com.lulobank.clients.services.outboundadapters.ClientsOutboundAdapter;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.model.LoanClientRequested;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class RiskScoringResultFromOnbordingEventHandler extends RiskScoringResultEvent {

  public RiskScoringResultFromOnbordingEventHandler(ClientsOutboundAdapter clientsOutboundAdapter) {
    super(clientsOutboundAdapter);
  }

  @Override
  LoanClientRequested getLoanRequested(ClientEntity clientEntity) {
    return clientEntity.getOnBoardingStatus().getLoanClientRequested();
  }

  private Predicate<ClientEntity> isLoanRequestedNotFinished =
      clientEntity ->
          !FINISH_ON_BOARDING.name().equals(clientEntity.getOnBoardingStatus().getCheckpoint());

  @Override
  void notifyLoanFinished(
      ClientsOutboundAdapter clientsOutboundAdapter,
      ClientEntity clientEntity,
      Map<String, Object> users) {
    notifyLoanOnBordingCreated.accept(clientsOutboundAdapter, users);
  }

  @Override
  Map<String, Object> getFirebaseFailParams(ClientEntity clientEntity, String detail) {
    return getFirebaseFailParamsOnboardingCredit(clientEntity, detail);
  }

  @Override
  void notifyLoanFailed(ClientsOutboundAdapter clientsOutboundAdapter, Map<String, Object> users) {
    notifyLoanOnBoardingFailed.accept(clientsOutboundAdapter, users);
  }

  @Override
  boolean isLoanRequestedNotFinished(ClientEntity clientEntity) {
    return isLoanRequestedNotFinished.test(clientEntity);
  }

  @Override
  Map<String, Object> getFirebaseNotification(ClientEntity clientEntity) {
    Map<String, Object> users = new HashMap<>();
    users.put("clientVerification", createClientVerificationFirebaseOKFromEntity(clientEntity));
    users.put(ID_CLIENT_KEY, clientEntity.getIdClient());
    return users;
  }
}
