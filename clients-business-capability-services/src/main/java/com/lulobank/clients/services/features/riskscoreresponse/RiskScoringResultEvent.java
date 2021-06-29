package com.lulobank.clients.services.features.riskscoreresponse;

import com.google.gson.Gson;
import com.lulobank.clients.services.events.RiskScoringResult;
import com.lulobank.clients.services.features.loanrequested.InitialOffersMapper;
import com.lulobank.clients.services.outboundadapters.ClientsOutboundAdapter;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.model.LoanClientRequested;
import com.lulobank.core.events.Event;
import com.lulobank.core.events.EventHandler;
import com.lulobank.credits.sdk.exceptions.InitialOffersException;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.lulobank.clients.services.utils.ClientErrorResultsEnum.ERROR_LOAN_IN_OFFER_GENERATION;
import static com.lulobank.clients.services.utils.ClientHelper.getHeadersClientCredentials;

public abstract class RiskScoringResultEvent implements EventHandler<Event<RiskScoringResult>> {

    private static final Logger log = LoggerFactory.getLogger(RiskScoringResultEvent.class);

    private ClientsOutboundAdapter clientsOutboundAdapter;

    private ClientEntity clientEntity;

    public RiskScoringResultEvent(ClientsOutboundAdapter clientsOutboundAdapter) {
        this.clientsOutboundAdapter = clientsOutboundAdapter;
    }

    @Override
    public void apply(Event<RiskScoringResult> event) {
        Option.of(clientEntity)
                .peek(entity -> log.info("Processing riskScoringResult event: {}", new Gson().toJson(event.getPayload())))
                .peek(processClient(event.getPayload()));
    }

    private Consumer<ClientEntity> processClient(RiskScoringResult event) {
        return client ->
                Try.of(() -> client)
                        .peek(economicInfo -> log.info("Processing client to build offers: {}", Encode.forJava(client.getIdClient())))
                        .map(ClientEntity::getEconomicInformation)
                        .filter(Objects::nonNull)
                        .map(economicInfo -> CreditRiskAnalysisMapper.INSTANCE.riskScoringResultToCreditRiskAnalysis(event))
                        .map(creditRiskAnalysis -> {
                            client.setCreditRiskAnalysis(creditRiskAnalysis);
                            return client;
                        })
                        .filter(validateLoanRequested())
                        .filter(this::isLoanRequestedNotFinished)
                        .mapTry(this::callInitialsOffers)
                        .peek(clientEntity -> notify(getFirebaseNotification(clientEntity)))
                        .peek(this::update)
                        .peek(clientEntity -> log.info("Risk score update for idClient : {}", Encode.forJava(clientEntity.getIdClient())))
                        .onFailure(InitialOffersException.class, exception -> handlerRetrofitError(clientEntity, exception))
                        .onFailure(error -> log.error("Error to try update risk Score for idClient : {} , msg : {} ", Encode.forJava(clientEntity.getIdClient()), error.getMessage()));
    }

    private ClientEntity callInitialsOffers(ClientEntity clientEntity) {
        InitialOffersMapper mapper=InitialOffersMapper.INSTANCE;
        clientsOutboundAdapter
                .getInitialOffersOperations()
                .initialOffers(
                        getHeadersClientCredentials(clientsOutboundAdapter, clientEntity.getIdClient()),
                        mapper.getOfferClientFrom(clientEntity, mapper.clientLoanRequested(getLoanRequested(clientEntity))),
                        clientEntity.getIdClient());
        return clientEntity;
    }

    private Predicate<ClientEntity> validateLoanRequested() {
        return clientEntity ->
                Option.of(getLoanRequested(clientEntity))
                        .map(exist -> true)
                        .getOrElse(() -> {
                            log.error("Error to try update risk Score for idClient {}, loan requested is not present ", Encode.forJava(clientEntity.getIdClient()));
                            return false;
                        });
    }


    private void notify(Map<String, Object> users) {
        notifyLoanFinished(clientsOutboundAdapter, clientEntity, users);
    }

    private void update(ClientEntity clientEntity) {
        clientsOutboundAdapter.getClientsRepository().save(clientEntity);
    }

    private void handlerRetrofitError(ClientEntity client, InitialOffersException e) {
        log.error(
                "Error while generating offers in RiskEvent : message {}, serviceCode: {} ,serviceMessage: {}, idClient: {}",
                e.getMessage(),
                e.getServiceCode(),
                e.getServiceMessage(),
                Encode.forJava(clientEntity.getIdClient()),
                e);
        notifyLoanFailed(
                clientsOutboundAdapter,
                getFirebaseFailParams(client, ERROR_LOAN_IN_OFFER_GENERATION.name()));
    }

    public void setClientEntity(ClientEntity clientEntity) {
        this.clientEntity = clientEntity;
    }

    abstract LoanClientRequested getLoanRequested(ClientEntity clientEntity);

    abstract void notifyLoanFinished(
            ClientsOutboundAdapter clientsOutboundAdapter,
            ClientEntity clientEntity,
            Map<String, Object> users);

    abstract Map<String, Object> getFirebaseFailParams(ClientEntity clientEntity, String detail);

    abstract void notifyLoanFailed(
            ClientsOutboundAdapter clientsOutboundAdapter, Map<String, Object> users);

    abstract boolean isLoanRequestedNotFinished(ClientEntity clientEntity);

    abstract Map<String, Object> getFirebaseNotification(ClientEntity clientEntity);
}
