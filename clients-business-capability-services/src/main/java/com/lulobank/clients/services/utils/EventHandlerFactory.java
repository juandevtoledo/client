package com.lulobank.clients.services.utils;

import static com.lulobank.clients.services.utils.LogMessages.EVENT_NOT_FOUND;

import com.lulobank.clients.services.events.CBSCreated;
import com.lulobank.clients.services.events.CheckIdentityBiometric;
import com.lulobank.clients.services.events.ClientVerificationResult;
import com.lulobank.clients.services.events.CreditAccepted;
import com.lulobank.clients.services.events.EmailVerified;
import com.lulobank.clients.services.events.UpdateClientAddressEvent;
import com.lulobank.clients.services.features.checkidentitybiometric.CheckIdentityBiometricHandler;
import com.lulobank.clients.services.features.onboardingclients.CBSCreatedEventHandler;
import com.lulobank.clients.services.features.clientverificationresult.ClientVerificationResultHandler;
import com.lulobank.clients.services.features.onboardingclients.EmailVerifiedEventHandler;
import com.lulobank.clients.services.features.onboardingclients.UpdateCreditAcceptHandler;
import com.lulobank.clients.services.features.profile.UpdateClientAddressEventHandler;
import com.lulobank.clients.services.features.profile.UpdateClientAddressEventUseCase;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.core.events.Event;
import com.lulobank.core.events.EventHandler;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventHandlerFactory {
  private static final Logger LOGGER = LoggerFactory.getLogger(EventHandlerFactory.class);
  private ClientsRepository clientsRepository;
  private ClientVerificationResultHandler
          clientVerificationResultHandler;
  private UpdateCreditAcceptHandler updateCreditAcceptHandler;
  private CheckIdentityBiometricHandler checkIdentityBiometricHandler;
  private UpdateClientAddressEventUseCase updateClientAddressEventUseCase;

  public EventHandlerFactory(
      ClientsRepository clientsRepository,
      ClientVerificationResultHandler
              clientVerificationResultHandler,
      UpdateCreditAcceptHandler updateCreditAcceptHandler,
      CheckIdentityBiometricHandler checkIdentityBiometricHandler,
      UpdateClientAddressEventUseCase updateClientAddressEventUseCase) {
    this.clientsRepository = clientsRepository;
    this.clientVerificationResultHandler =
            clientVerificationResultHandler;
    this.updateCreditAcceptHandler = updateCreditAcceptHandler;
    this.checkIdentityBiometricHandler = checkIdentityBiometricHandler;
    this.updateClientAddressEventUseCase = updateClientAddressEventUseCase;
  }

  public EventHandler createHandler(Event event, Map<String, Object> headers) {
    EventHandler eventHandler = null;
    String eventType = event.getEventType();
    if (EmailVerified.class.getSimpleName().equals(eventType)) {
      eventHandler = new EmailVerifiedEventHandler(clientsRepository);
    }
    if (CBSCreated.class.getSimpleName().equals(eventType)) {
      eventHandler = new CBSCreatedEventHandler(clientsRepository);
    }
    if (ClientVerificationResult.class.getSimpleName().equals(eventType)) {
      clientVerificationResultHandler.setHeadersSqs(headers);
      eventHandler = clientVerificationResultHandler;
    }
    if (CreditAccepted.class.getSimpleName().equals(eventType)) {
      eventHandler = updateCreditAcceptHandler;
    }
    if (CheckIdentityBiometric.class.getSimpleName().equals(eventType)) {
      checkIdentityBiometricHandler.setHeadersSqs(headers);
      eventHandler = checkIdentityBiometricHandler;
    }
    if (UpdateClientAddressEvent.class.getSimpleName().equals(eventType)) {
      eventHandler = new UpdateClientAddressEventHandler(updateClientAddressEventUseCase);
    }
    if (Objects.isNull(eventHandler)) {
      LOGGER.info(EVENT_NOT_FOUND.getMessage(), eventType);
    }
    return eventHandler;
  }
}
