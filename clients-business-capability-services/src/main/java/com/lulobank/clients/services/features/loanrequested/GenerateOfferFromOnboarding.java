package com.lulobank.clients.services.features.loanrequested;

import static com.lulobank.clients.services.features.onboardingclients.OnboardingClientsConverter.createClientVerificationFirebaseOKFromEntity;
import static com.lulobank.clients.services.utils.CheckPointsHelper.isCheckpointPresent;
import static com.lulobank.clients.services.utils.CheckPointsHelper.isClientVerificationStatus;
import static com.lulobank.clients.services.utils.ClientHelper.ID_CLIENT_KEY;
import static com.lulobank.clients.services.utils.ClientHelper.isAmountPresent;
import static com.lulobank.clients.services.utils.ClientHelper.isClientRiskAnalisisPresent;
import static com.lulobank.clients.services.utils.ClientHelper.notifyLoanOnBoardingFailed;
import static com.lulobank.clients.services.utils.ClientHelper.notifyLoanOnBordingCreated;

import com.lulobank.clients.services.outboundadapters.ClientsOutboundAdapter;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import java.util.HashMap;
import java.util.Map;

public class GenerateOfferFromOnboarding extends GenerateOffer {

  private ClientsOutboundAdapter clientsOutboundAdapter;

  public GenerateOfferFromOnboarding(ClientsOutboundAdapter clientsOutboundAdapter) {
    super(clientsOutboundAdapter);
    this.clientsOutboundAdapter = clientsOutboundAdapter;
  }

  @Override
  void notifyLoanFinished(ClientEntity clientEntity) {
    Map<String, Object> users = getFirebaseMessages(clientEntity);
    notifyLoanOnBordingCreated.accept(clientsOutboundAdapter, users);
  }

  @Override
  void notifyLoanFailed(ClientEntity clientEntity) {
    Map<String, Object> users = getFirebaseMessages(clientEntity);
    notifyLoanOnBoardingFailed.accept(clientsOutboundAdapter, users);
  }

  @Override
  Map<String, Object> getFirebaseMessages(ClientEntity clientEntity) {
    Map<String, Object> users = new HashMap<>();
    users.put(ID_CLIENT_KEY, clientEntity.getIdClient());
    users.put("clientVerification", createClientVerificationFirebaseOKFromEntity(clientEntity));
    return users;
  }

  @Override
  boolean validateUpdateOffers(ClientEntity clientEntity) {
    return isAmountPresent
        .and(isCheckpointPresent)
        .and(isClientVerificationStatus)
        .and(isClientRiskAnalisisPresent)
        .test(clientEntity);
  }
}
