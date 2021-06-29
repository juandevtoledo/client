package com.lulobank.clients.services.features.onboardingclients;

import com.lulobank.clients.services.events.EmailVerified;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.core.events.Event;
import com.lulobank.core.events.EventHandler;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailVerifiedEventHandler implements EventHandler<Event<EmailVerified>> {

  private ClientsRepository clientsRepository;
  private static final Logger logger = LoggerFactory.getLogger(EmailVerifiedEventHandler.class);

  public EmailVerifiedEventHandler(ClientsRepository clientsRepository) {
    this.clientsRepository = clientsRepository;
  }

  @Override
  public void apply(Event<EmailVerified> event) {
    if (event.getEventType().equals(EmailVerified.class.getSimpleName())) {
      EmailVerified emailVerified = event.getPayload();

      ClientEntity clientEntity =
          clientsRepository.findByIdClient(emailVerified.getIdClient()).orElse(null);

      Optional<ClientEntity> optionalEntity = Optional.ofNullable(clientEntity);
      optionalEntity.ifPresent(
          objClientEntity -> {
            objClientEntity.setEmailVerified(true);
            clientsRepository.save(objClientEntity);
            logger.info("Email Verified Update");
          });
    }
  }
}
