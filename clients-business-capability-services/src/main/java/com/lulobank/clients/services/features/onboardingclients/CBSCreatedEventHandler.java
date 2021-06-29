package com.lulobank.clients.services.features.onboardingclients;

import com.lulobank.clients.services.events.CBSCreated;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.core.events.Event;
import com.lulobank.core.events.EventHandler;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CBSCreatedEventHandler implements EventHandler<Event<CBSCreated>> {

  private ClientsRepository clientsRepository;
  private static final Logger logger = LoggerFactory.getLogger(CBSCreatedEventHandler.class);

  public CBSCreatedEventHandler(ClientsRepository clientsRepository) {
    this.clientsRepository = clientsRepository;
  }

  @Override
  public void apply(Event<CBSCreated> event) {
    // TODO: Deuda tecnica: Tenemos que diseñar esto de forma que avise al Front si fallo la
    // creación de la cuenta en Cognito y/o hacer 3 reintentos y avisar a operaciones:
    Optional<Event<CBSCreated>> optionalEvent = Optional.ofNullable(event);
    optionalEvent.ifPresent(
        objEventEntity -> {
          if (CBSCreated.class.getSimpleName().equals(event.getEventType())) {
            CBSCreated cbsCreated = event.getPayload();

            ClientEntity clientEntity =
                clientsRepository.findByIdClient(cbsCreated.getIdClient()).orElse(null);

            Optional<ClientEntity> optionalEntity = Optional.ofNullable(clientEntity);
            optionalEntity.ifPresent(
                objClientEntity -> {
                  objClientEntity.setIdCbs(cbsCreated.getIdCbs());
                  objClientEntity.setIdCbsHash(cbsCreated.getIdCbsHash());
                  clientsRepository.save(objClientEntity);
                  logger.info("CBS Update");
                });
          }
        });
  }
}
