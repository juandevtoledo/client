package com.lulobank.clients.services.inboundadapters;

import static org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy.ALWAYS;

import com.lulobank.clients.services.features.checkidentitybiometric.CheckIdentityBiometricHandler;
import com.lulobank.clients.services.features.clientverificationresult.ClientVerificationResultHandler;
import com.lulobank.clients.services.features.onboardingclients.UpdateCreditAcceptHandler;
import com.lulobank.clients.services.features.profile.UpdateClientAddressEventUseCase;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.clients.services.utils.EventHandlerFactory;
import com.lulobank.clients.services.utils.EventUtils;
import com.lulobank.core.events.Event;
import com.lulobank.core.events.EventHandler;
import java.util.Map;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;


@Controller
@Slf4j
public class SqsClientListenerAdapter {
  public static final String PACKAGE_SERVICES = "com.lulobank.clients.services";

  private ClientsRepository repository;
  private ClientVerificationResultHandler clientVerificationResultHandler;
  private UpdateCreditAcceptHandler updateCreditAcceptHandler;
  private CheckIdentityBiometricHandler checkIdentityBiometricHandler;
  private UpdateClientAddressEventUseCase updateClientAddressEventUseCase;


  public SqsClientListenerAdapter(
      ClientsRepository repository,
      ClientVerificationResultHandler
              clientVerificationResultHandler,
      UpdateCreditAcceptHandler updateCreditAcceptHandler,
      CheckIdentityBiometricHandler checkIdentityBiometricHandler,
      UpdateClientAddressEventUseCase updateClientAddressEventUseCase) {
    this.repository = repository;
    this.clientVerificationResultHandler =
            clientVerificationResultHandler;
    this.updateCreditAcceptHandler = updateCreditAcceptHandler;
    this.checkIdentityBiometricHandler = checkIdentityBiometricHandler;
    this.updateClientAddressEventUseCase = updateClientAddressEventUseCase;
  }

  @SqsListener(value = "${cloud.aws.sqs.client-events}", deletionPolicy = ALWAYS)
  public void getMessage(@Headers Map<String, Object> headers, @Payload final String eventString) {
    Event event = EventUtils.getEvent(eventString, PACKAGE_SERVICES);
    log.info("Process Clients event msg : {} ",eventString);
    if (!Objects.isNull(event)) {
      EventHandler handler =
          new EventHandlerFactory(
                  repository,
                  clientVerificationResultHandler,
                  updateCreditAcceptHandler,
                  checkIdentityBiometricHandler,
                  updateClientAddressEventUseCase)
              .createHandler(event, headers);
      if (!Objects.isNull(handler)) {
        handler.apply(event);
      }
    }
  }
}
