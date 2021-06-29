package com.lulobank.clients.services.features.riskscoreresponse;

import static com.lulobank.clients.services.utils.ClientHelper.isLoanRequestedFromHome;
import static com.lulobank.clients.services.utils.ClientHelper.isOnboardingProductSelectedCredit;
import static com.lulobank.clients.services.utils.LogMessages.NOT_LOAN_AMOUNT_TO_CLIENT;
import static com.lulobank.clients.services.utils.LogMessages.RISK_ENGINE_RESPONSE;

import com.lulobank.clients.services.events.RiskScoringResult;
import com.lulobank.clients.services.exception.ClientNotFoundException;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.core.events.Event;
import java.util.Optional;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RiskScoringResultEventFactory {
  private static final Logger LOGGER = LoggerFactory.getLogger(RiskScoringResultEventFactory.class);
  private ClientsRepository clientsRepository;
  private RiskScoringResultFromHomeEventHandler riskScoringResultFromHomeEventHandler;
  private RiskScoringResultFromOnbordingEventHandler riskScoringResultFromOnbordingEventHandler;

  public RiskScoringResultEventFactory(
      ClientsRepository clientsRepository,
      RiskScoringResultFromHomeEventHandler riskScoringResultFromHomeEventHandler,
      RiskScoringResultFromOnbordingEventHandler riskScoringResultFromOnbordingEventHandler) {
    this.clientsRepository = clientsRepository;
    this.riskScoringResultFromHomeEventHandler = riskScoringResultFromHomeEventHandler;
    this.riskScoringResultFromOnbordingEventHandler = riskScoringResultFromOnbordingEventHandler;
  }

  public RiskScoringResultEvent createHandler(Event<RiskScoringResult> event) {
    LOGGER.info(RISK_ENGINE_RESPONSE.getMessage(), Encode.forJava(event.getId()));
    return clientsRepository
        .findByIdClient(event.getId())
        .map(geRiskEvent)
        .orElseThrow(ClientNotFoundException::new);
  }

  private Function<ClientEntity, RiskScoringResultEvent> geRiskEvent =
      clientEntity -> {
        RiskScoringResultEvent riskScoringResultEvent = null;
        if (isLoanRequestedFromHome.test(clientEntity)) {
          riskScoringResultEvent =
              getRiskScoringResultEvent(riskScoringResultFromHomeEventHandler, clientEntity);
        } else if (isOnboardingProductSelectedCredit.test(clientEntity)) {
          riskScoringResultEvent =
              getRiskScoringResultEvent(riskScoringResultFromOnbordingEventHandler, clientEntity);
        }
        return Optional.ofNullable(riskScoringResultEvent)
            .orElseThrow(
                () ->
                    new UnsupportedOperationException(
                        NOT_LOAN_AMOUNT_TO_CLIENT.getMessage().concat(clientEntity.getIdClient())));
      };

  @NotNull
  private RiskScoringResultEvent getRiskScoringResultEvent(
      RiskScoringResultEvent riskScoringResultEvent, ClientEntity clientEntity) {
    riskScoringResultEvent.setClientEntity(clientEntity);
    return riskScoringResultEvent;
  }
}
