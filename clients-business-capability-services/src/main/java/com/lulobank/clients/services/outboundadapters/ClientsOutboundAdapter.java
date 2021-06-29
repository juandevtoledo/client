package com.lulobank.clients.services.outboundadapters;

import com.google.firebase.database.DatabaseReference;
import com.lulobank.clientalerts.sdk.operations.impl.RetrofitClientNotificationsOperations;
import com.lulobank.clients.services.actions.MessageToSQSCheckBiometricIdentity;
import com.lulobank.clients.services.features.onboardingclients.action.MessageToNotifySQSClients;
import com.lulobank.clients.services.features.onboardingclients.action.MessageToNotifySQSEconomicInformation;
import com.lulobank.clients.services.features.onboardingclients.action.MessageToNotifySQSRiskEngine;
import com.lulobank.clients.services.features.onboardingclients.action.MessageToNotifySQSRiskEngineResponse;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.clients.services.utils.CognitoProperties;
import com.lulobank.clients.services.utils.LuloUserTokenGenerator;
import com.lulobank.credits.sdk.operations.InitialOffersOperations;
import com.lulobank.otp.sdk.operations.impl.RetrofitOtpOperations;
import com.lulobank.savingsaccounts.sdk.operations.ISavingsAccount;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class ClientsOutboundAdapter {
  @Autowired private ClientsRepository clientsRepository;
  @Autowired private ISavingsAccount savingsAccount;
  @Autowired private MessageToNotifySQSRiskEngine messageToNotifySQSRiskEngine;
  @Autowired private MessageToNotifySQSClients messageToNotifySQSClients;
  @Autowired private MessageToNotifySQSRiskEngineResponse messageToNotifySQSRiskEngineResponse;
  @Autowired private DatabaseReference databaseReference;
  @Autowired private InitialOffersOperations initialOffersOperations;
  @Autowired private CognitoProperties cognitoProperties;
  @Autowired private RetrofitOtpOperations retrofitOtpOperations;
  @Autowired private RetrofitClientNotificationsOperations retrofitClientNotificationsOperations;
  @Autowired private QueueMessagingTemplate queueMessagingTemplate;
  @Autowired private LuloUserTokenGenerator luloUserTokenGenerator;
  @Autowired private MessageToSQSCheckBiometricIdentity messageToSQSCheckBiometricIdentity;
  @Autowired private MessageToNotifySQSEconomicInformation messageToNotifySQSEconomicInformation;

}
