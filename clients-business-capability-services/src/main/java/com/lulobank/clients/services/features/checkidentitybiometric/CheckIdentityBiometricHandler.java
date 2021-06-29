package com.lulobank.clients.services.features.checkidentitybiometric;

import static com.lulobank.clients.services.utils.IdentityBiometricStatus.IN_PROGRESS;
import static com.lulobank.clients.services.utils.IdentityBiometricStatus.TIMEOUT_ADOTECH;
import static com.lulobank.clients.services.utils.SQSUtil.retryEvent;
import static java.lang.Boolean.TRUE;

import com.lulobank.clients.services.events.CheckIdentityBiometric;
import com.lulobank.clients.services.features.RetriesOption;
import com.lulobank.clients.services.outboundadapters.ClientsOutboundAdapter;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.model.IdentityBiometric;
import com.lulobank.clients.services.utils.ClientHelper;
import com.lulobank.clients.services.utils.Constants;
import com.lulobank.clients.services.utils.LogMessages;
import com.lulobank.core.Response;
import com.lulobank.core.events.Event;
import com.lulobank.core.events.EventHandler;
import io.vavr.control.Option;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.owasp.encoder.Encode;

@Slf4j
public class CheckIdentityBiometricHandler implements EventHandler<Event<CheckIdentityBiometric>> {
  private ClientsOutboundAdapter clientsOutboundAdapter;
  private RetriesOption retriesOption;
  private Map<String, Object> headersSqs;

  public CheckIdentityBiometricHandler(
      ClientsOutboundAdapter clientsOutboundAdapter, RetriesOption retriesOption) {
    this.clientsOutboundAdapter = clientsOutboundAdapter;
    this.retriesOption = retriesOption;
  }

  @Override
  public void apply(Event<CheckIdentityBiometric> checkIdentityBiometricEvent) {

    Option.of(checkIdentityBiometricEvent.getPayload())
        .peek(
            event ->
                Option.ofOptional(getClientByIdentityBiometric(event))
                    .peek(processEvent(checkIdentityBiometricEvent)));
  }

  @NotNull
  private Consumer<ClientEntity> processEvent(
      Event<CheckIdentityBiometric> checkIdentityBiometricEvent) {
    return clientEntity -> {
      if (!TRUE.equals(clientEntity.getResetBiometric())) {
        if (retryEvent(checkIdentityBiometricEvent.getPayload(), headersSqs, retriesOption)) {
          sendToQueue(checkIdentityBiometricEvent);
        } else {
          notifyKOFirebase(clientEntity);
          saveAsBiometricTimeout(clientEntity);
        }
      }
    };
  }

  private void saveAsBiometricTimeout(ClientEntity clientEntity) {
    clientEntity.getIdentityBiometric().setStatus(TIMEOUT_ADOTECH.name());
    clientsOutboundAdapter.getClientsRepository().save(clientEntity);
  }

  private void notifyKOFirebase(ClientEntity client) {
    Map<String, Object> users = new HashMap<>();
    users.put(ClientHelper.CLIENT_VERIFICATION, ClientHelper.getClientKOBiometric(client));
    ClientHelper.getDatabaseReferenceOnBoarding(clientsOutboundAdapter, client.getIdClient())
            .mapTry(databaseReference -> databaseReference.updateChildrenAsync(users).get(Constants.TIME_OUT_FIREBASE, TimeUnit.SECONDS))
            .onFailure(error -> log.error(LogMessages.ERROR_UPDATE_FIREBASE.getMessage(), error.getMessage(), client.getIdClient()));
    log.info(LogMessages.CLIENT_REJECTED_FIREBASE.getMessage(), Encode.forJava(client.getIdClient()));
  }

  private void sendToQueue(Event<CheckIdentityBiometric> checkIdentityBiometricEvent) {
    CheckIdentityBiometric checkIdentityBiometric = checkIdentityBiometricEvent.getPayload();
    clientsOutboundAdapter
        .getMessageToSQSCheckBiometricIdentity()
        .run(new Response(checkIdentityBiometricEvent.getId()), checkIdentityBiometric);
  }

  private Optional<ClientEntity> getClientByIdentityBiometric(CheckIdentityBiometric event) {
    return clientsOutboundAdapter
        .getClientsRepository()
        .findByIdClientAndIdentityBiometric(event.getIdClient(), getIdentityBiometric(event));
  }

  @NotNull
  private IdentityBiometric getIdentityBiometric(CheckIdentityBiometric checkIdentityBiometric) {
    IdentityBiometric identityBiometric = new IdentityBiometric();
    identityBiometric.setStatus(IN_PROGRESS.name());
    identityBiometric.setIdTransaction(checkIdentityBiometric.getIdTransactionBiometric());
    return identityBiometric;
  }

  public void setHeadersSqs(Map<String, Object> headersSqs) {
    this.headersSqs = headersSqs;
  }
}
