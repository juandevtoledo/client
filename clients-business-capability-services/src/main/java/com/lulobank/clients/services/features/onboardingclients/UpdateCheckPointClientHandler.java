package com.lulobank.clients.services.features.onboardingclients;

import static com.lulobank.clients.services.utils.CheckPointsHelper.getGetCheckPointFromClientEntity;
import static com.lulobank.clients.services.utils.ClientErrorResultsEnum.BAD_PAYLOAD;
import static com.lulobank.clients.services.utils.ClientErrorResultsEnum.CHECKPOINT_INVALID;
import static org.apache.commons.lang3.EnumUtils.isValidEnum;

import com.lulobank.clients.sdk.operations.util.CheckPoints;
import com.lulobank.clients.services.events.CheckPointClient;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.core.events.Event;
import com.lulobank.core.events.EventHandler;
import com.lulobank.utils.exception.ServiceException;
import java.util.Optional;
import java.util.function.BiPredicate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class UpdateCheckPointClientHandler<T extends CheckPointClient>
    implements EventHandler<Event<T>> {

  private final ClientsRepository clientsRepository;

  public UpdateCheckPointClientHandler(ClientsRepository clientsRepository) {
    this.clientsRepository = clientsRepository;
  }

  @Override
  public void apply(Event<T> checkPointClientEvent) {
    Optional.ofNullable(checkPointClientEvent)
        .ifPresent(
            event -> {
              try {
                CheckPointClient checkPointClient =
                    validatePayload((Event<CheckPointClient>) event);
                ClientEntity clientEntity = getClientEntity((T) checkPointClient);
                validateCheckPointToUpdate(clientEntity, event.getPayload().getCheckpoint());
                clientEntity.getOnBoardingStatus().setCheckpoint(checkPointClient.getCheckpoint());
                clientsRepository.save(clientEntity);
                log.info("Update Checkpoint to idClient : {}, checkPoint : {}",clientEntity.getIdClient(),event.getPayload().getCheckpoint());
              } catch (ServiceException e) {
                log.error("Error while processing event to update checkpoint , EventType: {}, idEvent : {}, Msg : {}  ",checkPointClientEvent.getEventType(),
                        checkPointClientEvent.getId(),e.getMessage(), e);
              }
            });
  }

  private CheckPointClient validatePayload(Event<CheckPointClient> checkPointClientEvent) {
    CheckPointClient payload =
        Optional.ofNullable(checkPointClientEvent.getPayload())
            .orElseThrow(() -> new ServiceException(BAD_PAYLOAD.name()));
    if (!isValidEnum(CheckPoints.class, payload.getCheckpoint())) {
      throw new ServiceException(CHECKPOINT_INVALID.name());
    }
    return payload;
  }

  abstract ClientEntity getClientEntity(T checkPointClient);

  private void validateCheckPointToUpdate(ClientEntity clientEntity, String checkPointsToUpdate) {
    CheckPoints checkPointsClientEntity = getGetCheckPointFromClientEntity.apply(clientEntity);
    if (newCheckpointValid
        .negate()
        .test(CheckPoints.valueOf(checkPointsToUpdate), checkPointsClientEntity)) {
      throw new ServiceException(CHECKPOINT_INVALID.name());
    }
  }

  private BiPredicate<CheckPoints, CheckPoints> newCheckpointValid =
      (checkPointsToUpdate, checkPointsClientEntity) ->
          (checkPointsToUpdate.getOrder() > checkPointsClientEntity.getOrder());

  public ClientsRepository getClientsRepository() {
    return clientsRepository;
  }
}
