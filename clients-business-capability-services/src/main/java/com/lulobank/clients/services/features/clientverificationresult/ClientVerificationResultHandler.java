package com.lulobank.clients.services.features.clientverificationresult;

import com.lulobank.clients.services.application.port.out.reporting.ReportingMessagingPort;
import com.lulobank.clients.services.events.ClientVerificationResult;
import com.lulobank.clients.services.exception.DigitalEvidenceException;
import com.lulobank.clients.services.features.RetriesOption;
import com.lulobank.clients.services.features.clientverificationresult.mapper.ClientEntityMapper;
import com.lulobank.clients.services.features.clientverificationresult.mapper.IdentityBiometricMapper;
import com.lulobank.clients.services.outboundadapters.ClientsOutboundAdapter;
import com.lulobank.clients.services.ports.out.DigitalEvidenceService;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.IdentityBiometricV3;
import com.lulobank.clients.v3.util.DigitalEvidenceTypes;
import com.lulobank.core.events.Event;
import com.lulobank.core.events.EventHandler;
import io.vavr.concurrent.Future;
import io.vavr.control.Option;
import lombok.CustomLog;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.owasp.encoder.Encode;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;

import static com.lulobank.clients.sdk.operations.util.CheckPoints.BLACKLIST_STARTED;
import static com.lulobank.clients.services.domain.StateBlackList.BIOMETRY_FAILED;
import static com.lulobank.clients.services.domain.StateBlackList.NON_BLACKLISTED;
import static com.lulobank.clients.services.domain.StateBlackList.STARTED;
import static com.lulobank.clients.services.features.clientverificationresult.MessageSqsHelper.sendToSqs;
import static com.lulobank.clients.services.features.clientverificationresult.NotificationHelper.notifyBiometricReject;
import static com.lulobank.clients.services.features.clientverificationresult.NotificationHelper.notifyDigitalEvidenceError;
import static com.lulobank.clients.services.features.clientverificationresult.NotificationHelper.notifyIdCardExist;
import static com.lulobank.clients.services.utils.BiometricResultCodes.isSuccessfulStatus;
import static com.lulobank.clients.services.utils.ClientHelper.getHeadersClientCredentials;
import static com.lulobank.clients.services.utils.IdentityBiometricStatus.ALREADY_CLIENT_EXISTS;
import static com.lulobank.clients.services.utils.IdentityBiometricStatus.FINISHED;
import static com.lulobank.clients.services.utils.SQSUtil.retryEvent;

@CustomLog
public class ClientVerificationResultHandler implements EventHandler<Event<ClientVerificationResult>> {

    private ClientsOutboundAdapter clientsOutboundAdapter;
    private RetriesOption mobileResponseRetriesOption;
    private Map<String, Object> headersSqs;
    private ClientsV3Repository clientsV3Repository;
    private final ReportingMessagingPort reportingMessagingPort;
    private final NonBlacklistedProcessService nonBlacklistedProcessService;
    private final BlacklistedProcessService blacklistedProcessService;
    private final DigitalEvidenceService digitalEvidenceService;

    public ClientVerificationResultHandler(ClientsOutboundAdapter clientsOutboundAdapter,
                                           RetriesOption mobileResponseRetriesOption,
                                           ClientsV3Repository clientsV3Repository,
                                           NonBlacklistedProcessService nonBlacklistedProcessService,
                                           BlacklistedProcessService blacklistedProcessService,
                                           ReportingMessagingPort reportingMessagingPort,
                                           DigitalEvidenceService digitalEvidenceService) {
        this.clientsOutboundAdapter = clientsOutboundAdapter;
        this.mobileResponseRetriesOption = mobileResponseRetriesOption;
        this.clientsV3Repository = clientsV3Repository;
        this.blacklistedProcessService = blacklistedProcessService;
        this.nonBlacklistedProcessService = nonBlacklistedProcessService;
        this.reportingMessagingPort = reportingMessagingPort;
        this.digitalEvidenceService = digitalEvidenceService;
    }

    @Override
    public void apply(Event<ClientVerificationResult> event) {
        Option.of(event.getPayload())
                .peek(logStarProcess())
                .peek(payload -> Future.run(() -> start(payload)));
    }

    public void start(ClientVerificationResult payload) {
        getClientEntity(payload)
                .peek(idCardOnDatabase(payload))
                .onEmpty(() -> handlerIdBiometricNotFound(payload));
    }

    private Consumer<ClientsV3Entity> idCardOnDatabase(ClientVerificationResult payload) {
        return clientEntity ->
                findByIdCard(payload)
                        .peek(logClientExist(payload))
                        .peek(clientEntityIdCard -> handlerIdCardExists(clientEntity, payload))
                        .onEmpty(() -> verifyBiometricResult(payload).accept(clientEntity));
    }

    @NotNull
    @Contract(pure = true)
    private Consumer<ClientsV3Entity> verifyBiometricResult(ClientVerificationResult payload) {
        return clientEntity -> Option.of(payload.getTransactionState())
                .filter(transactionState -> isSuccessfulStatus(transactionState.getId()))
                .peek(transactionState -> log.info("Identity Biometric validation successful. IdTransactionBiometric: {}",
                        Encode.forJava(payload.getIdTransactionBiometric())))
                .peek(transactionState -> processBlacklistEvent(payload).accept(clientEntity))
                .onEmpty(() -> rejectByBiometry(clientEntity, payload));
    }

    private Consumer<ClientsV3Entity> processBlacklistEvent(ClientVerificationResult payload) {
        return clientEntity ->
                Option.of(STARTED.name().equals(payload.getBlacklist().getStatus()))
                        .filter(Boolean::booleanValue)
                        .map(ok -> ClientEntityMapper.INSTANCE.clientEntityFrom(payload, clientEntity))
                        .peek(setCheckpointBlackList(BLACKLIST_STARTED.name()))
                        .peek(entity -> entity.setIdentityProcessed(true))
                        .peek(clientsV3Entity -> clientsV3Repository.updateOnBoarding(clientsV3Entity))
                        .onEmpty(() -> processNonBlacklisted(clientEntity, payload));
    }

    private Option<ClientsV3Entity> processNonBlacklisted(ClientsV3Entity clientEntity, ClientVerificationResult payload) {
        return Option.of(clientEntity)
                .peek(client -> clientEntity.setDigitalStorageStatus(createDigitalEvidence(clientEntity)))
                .peek(client -> sendDigitalEvidenceDocuments(clientEntity))
                .filter(client -> isIdentityOk(payload))
                .peek(ok -> nonBlacklistedProcessService.emitEvent(clientEntity, payload))
                .onEmpty(() -> blacklistedProcessService.rejectClient(clientEntity, payload));
    }

    private Boolean createDigitalEvidence(ClientsV3Entity client) {
        return digitalEvidenceService.saveDigitalEvidence(getHeadersClientCredentials(clientsOutboundAdapter,
                client.getIdClient()), client, DigitalEvidenceTypes.APP)
                .map(r -> true)
                .onFailure(DigitalEvidenceException.class, e-> handleExceptionDigitalEvidence(e, client))
                .getOrElse(false);
    }

    private Option<ClientsV3Entity> sendDigitalEvidenceDocuments(ClientsV3Entity clientEntity) {
        return Option.of(clientEntity)
                .peek(clientsV3Entity -> reportingMessagingPort.sendBlacklistedDocuments(clientEntity)
                        .onFailure(t -> log.error("Error sending blacklisted digital evidence documents.")));
    }

    private void handleExceptionDigitalEvidence(DigitalEvidenceException e, ClientsV3Entity client) {
        Option.of(client)
                .peek(notifyDigitalEvidenceError(clientsOutboundAdapter))
                .peek(clientEntity -> log.error("Error creating digital evidence for idClient{}. Error {}.",
                        client.getIdClient(), e));
    }

    private boolean isIdentityOk(ClientVerificationResult payload) {
        return isNotBlackList(payload.getBlacklist().getStatus())
                && isSuccessfulStatus(payload.getTransactionState().getId());
    }

    private void rejectByBiometry(ClientsV3Entity clientEntity, ClientVerificationResult payload) {
        clientEntity.getIdentityBiometric().setTransactionState(IdentityBiometricMapper.INSTANCE.transactionStateV3From(payload.getTransactionState()));
        notifyBiometricReject(clientsOutboundAdapter)
                .andThen(update)
                .accept(clientEntity);
    }

    private void handlerIdBiometricNotFound(ClientVerificationResult payload) {
        log.error("Id Biometric Not Found , IdTransactionBiometric: {}, state: {}", Encode.forJava(payload.getIdTransactionBiometric()),
                payload.getTransactionState().getStateName());
        if (retryEvent(payload, headersSqs, mobileResponseRetriesOption)) {
            sendToSqs(payload, clientsOutboundAdapter);
        } else {
            log.error("Id Transaction Biometric impossible to update, IdTransactionBiometric : {} ", Encode.forJava(payload.getIdTransactionBiometric()));
        }
    }

    private void handlerIdCardExists(ClientsV3Entity clientEntity, ClientVerificationResult payload) {
        if (STARTED.name().equals(payload.getBlacklist().getStatus())) {
            log.error("Id Card is present, idCard: {} , IdTransactionBiometric: {} ",
                    Encode.forJava(payload.getClientPersonalInformation().getIdDocument().getIdCard()), Encode.forJava(payload.getIdTransactionBiometric()));
            setIdCardExist
                    .andThen(update)
                    .andThen(notifyIdCardExist(clientsOutboundAdapter))
                    .accept(clientEntity);
        } else {
            verifyBiometricResult(payload).accept(clientEntity);
        }
    }

    private final Consumer<ClientsV3Entity> setIdCardExist = clientEntity ->
            clientEntity.getIdentityBiometric().setStatus(ALREADY_CLIENT_EXISTS.name());

    private Option<ClientsV3Entity> getClientEntity(ClientVerificationResult clientVerificationResult) {
        IdentityBiometricV3 identityBiometric = IdentityBiometricMapper.INSTANCE.identityBiometricFrom(clientVerificationResult);

        if (!isStartedOrBiometryFailed(clientVerificationResult)) {
            identityBiometric.setStatus(FINISHED.name());
            identityBiometric.setTransactionState(IdentityBiometricMapper.INSTANCE.transactionStateV3From(clientVerificationResult.getTransactionState()));
        }

        return clientsV3Repository.findByIdentityBiometric(identityBiometric);

    }

    private boolean isStartedOrBiometryFailed(ClientVerificationResult payload) {
        return Arrays.asList(STARTED.name(), BIOMETRY_FAILED.name()).contains(payload.getBlacklist().getStatus());
    }

    private Option<ClientsV3Entity> findByIdCard(ClientVerificationResult payload) {
        return clientsV3Repository
                .findByIdCard(payload.getClientPersonalInformation().getIdDocument().getIdCard());
    }

    private Consumer<ClientsV3Entity> setCheckpointBlackList(String checkpoint) {
        return clientEntity ->
                clientEntity.getOnBoardingStatus().setCheckpoint(checkpoint);
    }

    private final Consumer<ClientsV3Entity> update = clientEntity -> clientsV3Repository.save(clientEntity);

    private Consumer<ClientsV3Entity> logClientExist(ClientVerificationResult payload) {
        return clientExist ->
                log.info("For this IdTransactionBiometric: {} , exist this idClient: {} ", Encode.forJava(payload.getIdTransactionBiometric()), Encode.forJava(clientExist.getIdClient()));
    }

    private Consumer<ClientVerificationResult> logStarProcess() {
        return payload -> log.info(
                "Start process to Identity Biometric report, IdTransactionBiometric : {}, Status: {}",
                payload.getIdTransactionBiometric(),
                payload.getTransactionState().getStateName());
    }

    private boolean isNotBlackList(String state) {
        return NON_BLACKLISTED.name().equals(state);
    }

    public void setHeadersSqs(Map<String, Object> headersSqs) {
        this.headersSqs = headersSqs;
    }
}
