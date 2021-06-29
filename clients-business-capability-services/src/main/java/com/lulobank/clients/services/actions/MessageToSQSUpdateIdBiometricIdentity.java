package com.lulobank.clients.services.actions;

import com.lulobank.clients.services.events.CheckIdentityBiometric;
import com.lulobank.clients.services.features.RetriesOption;
import com.lulobank.clients.services.features.identitybiometric.model.UpdateIdTransactionBiometric;
import com.lulobank.clients.services.outboundadapters.SendMessageToSQS;
import com.lulobank.core.Response;
import com.lulobank.core.events.Event;
import com.lulobank.core.utils.EventUtils;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

public class MessageToSQSUpdateIdBiometricIdentity
    extends SendMessageToSQS<UpdateIdTransactionBiometric> {

  private RetriesOption identityBiometricRetriesOption;

  public MessageToSQSUpdateIdBiometricIdentity(
      QueueMessagingTemplate queueMessagingTemplate,
      String sqsEndPoint,
      RetriesOption identityBiometricRetriesOption) {
    super(queueMessagingTemplate, sqsEndPoint);
    this.identityBiometricRetriesOption = identityBiometricRetriesOption;
  }

  @Override
  public Event buildEvent(Response response, UpdateIdTransactionBiometric command) {
    Event event = null;

    if (validateRetries(identityBiometricRetriesOption)) {
      event = new EventUtils<CheckIdentityBiometric>().getEvent(getCheckBiometricIdentity(command));
      event.setId(UUID.randomUUID().toString());
    }

    return event;
  }

  private boolean validateRetries(RetriesOption identityBiometricRetriesOption) {
    return identityBiometricRetriesOption.getMaxRetries().compareTo(0) > 0;
  }

  @NotNull
  private CheckIdentityBiometric getCheckBiometricIdentity(UpdateIdTransactionBiometric command) {
    CheckIdentityBiometric checkIdentityBiometric = new CheckIdentityBiometric();
    checkIdentityBiometric.setIdClient(command.getIdClient());
    checkIdentityBiometric.setIdTransactionBiometric(command.getIdTransactionBiometric());
    return checkIdentityBiometric;
  }
}
