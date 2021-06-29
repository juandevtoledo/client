package com.lulobank.clients.services.features.clientverificationresult;

import com.lulobank.clients.services.application.port.out.clientnotify.ClientNotifyService;
import com.lulobank.clients.services.domain.RiskLevelBlackList;
import com.lulobank.clients.services.events.ClientVerificationResult;
import com.lulobank.clients.services.exception.CreateCustomerException;
import com.lulobank.clients.services.features.clientverificationresult.mapper.IdentityBiometricMapper;
import com.lulobank.clients.services.outboundadapters.ClientsOutboundAdapter;
import com.lulobank.clients.services.ports.out.CustomerService;
import com.lulobank.clients.services.utils.StatusClientVerificationFirebaseEnum;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import io.vavr.API;
import io.vavr.control.Option;
import lombok.CustomLog;

import java.time.LocalDateTime;
import java.util.function.Consumer;

import static com.lulobank.clients.sdk.operations.util.CheckPoints.BLACKLISTED;
import static com.lulobank.clients.services.domain.StateBlackList.FAILED;
import static com.lulobank.clients.services.features.clientverificationresult.NotificationHelper.notifyBiometricReject;
import static com.lulobank.clients.services.features.clientverificationresult.NotificationHelper.notifyBlackListFailed;
import static com.lulobank.clients.services.features.clientverificationresult.NotificationHelper.notifyBlackListReject;
import static com.lulobank.clients.services.features.clientverificationresult.NotificationHelper.notifyCreateCustomerError;
import static com.lulobank.clients.services.utils.ClientHelper.getHeadersClientCredentials;
import static com.lulobank.clients.services.utils.StatusClientVerificationFirebaseEnum.KO_BLACKLIST_HIGH_RISK;
import static com.lulobank.clients.services.utils.StatusClientVerificationFirebaseEnum.KO_BLACKLIST_MEDIUM_RISK;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.run;

@CustomLog
public class BlacklistedProcessService {

    private ClientsOutboundAdapter clientsOutboundAdapter;
    private ClientNotifyService clientNotifyService;
    private ClientsV3Repository clientsV3Repository;
    private CustomerService customerService;


    public BlacklistedProcessService(ClientsOutboundAdapter clientsOutboundAdapter,
                                     ClientNotifyService clientNotifyService,
                                     ClientsV3Repository clientsV3Repository,
                                     CustomerService customerService) {
        this.clientsOutboundAdapter = clientsOutboundAdapter;
        this.clientNotifyService = clientNotifyService;
        this.clientsV3Repository = clientsV3Repository;
        this.customerService = customerService;
    }

    public void rejectClient(ClientsV3Entity clientEntity, ClientVerificationResult payload) {
        Option.of(clientEntity)
                .peek(client -> client.setCustomerCreatedStatus(createCustomer(client)))
                .filter(clientsV3Entity -> isBlackList(payload.getBlacklist().getStatus()))
                .peek(isBlacklist -> log.info("Client with idCard: {} is BLACKLISTED, risk level {}. IdTransactionBiometric: {}",
                        clientEntity.getIdCard(), payload.getBlacklist().getResultRiskLevel(),
                        payload.getIdTransactionBiometric()))
                .peek(isBlacklist -> rejectByBlackList(clientEntity, payload))
                .onEmpty(() -> processBlacklistFailed(clientEntity, payload));
    }

    private boolean createCustomer(ClientsV3Entity client) {
        return customerService.createUserCustomer(getHeadersClientCredentials(clientsOutboundAdapter, client.getIdClient()),
                client)
                .map(r -> true)
                .onFailure(CreateCustomerException.class, e -> handleExceptionCustomerService(e, client))
                .getOrElse(false);
    }

    private void handleExceptionCustomerService(CreateCustomerException e, ClientsV3Entity client) {
        Option.of(client)
                .peek(notifyCreateCustomerError(clientsOutboundAdapter))
                .peek(clientEntity -> log.error("Error creating customer for idClient{}. Error {}.",
                        client.getIdClient(), e));
    }


    private boolean isBlackList(String state) {
        return BLACKLISTED.name().equals(state);
    }

    private void rejectByBlackList(ClientsV3Entity clientEntity, ClientVerificationResult payload) {
        clientEntity.setBlackListState(BLACKLISTED.name());
        clientEntity.setIdentityProcessed(true);
        clientEntity.setBlackListRiskLevel(payload.getBlacklist().getResultRiskLevel());
        clientEntity.setBlackListDate(LocalDateTime.parse(payload.getBlacklist().getReportDate()));
        API.Match(clientEntity.getBlackListRiskLevel()).of(
                Case($(RiskLevelBlackList.MID_RISK.getLevel()), () -> run(() -> rejectBlacklist(clientEntity, KO_BLACKLIST_MEDIUM_RISK))),
                Case($(RiskLevelBlackList.HIGH_RISK.getLevel()), () -> run(() -> rejectBlacklist(clientEntity, KO_BLACKLIST_HIGH_RISK)))
        );

    }

    private void rejectBlacklist(ClientsV3Entity clientEntity, StatusClientVerificationFirebaseEnum koBlacklistMediumRisk) {
        notifyBlackListReject(clientsOutboundAdapter, koBlacklistMediumRisk)
                .andThen(setCheckpointBlackList(BLACKLISTED.name()))
                .andThen(updateClientEntityRepository)
                .andThen(sendBlacklistNotification)
                .accept(clientEntity);
    }

    private void processBlacklistFailed(ClientsV3Entity clientEntity, ClientVerificationResult payload) {
        Option.of(FAILED.name().equals(payload.getBlacklist().getStatus()))
                .filter(Boolean::booleanValue)
                .peek(ok -> log.info("Blacklist validation for Client with idCard: {} is FAILED. IdTransactionBiometric: {}",
                        clientEntity.getIdCard(), payload.getIdTransactionBiometric()))
                .map(ok -> clientEntity)
                .peek(clientEntity1 -> clientEntity1.setBlackListState(FAILED.name()))
                .peek(updateClientEntityRepository)
                .peek(notifyBlackListFailed(clientsOutboundAdapter))
                .onEmpty(() -> rejectByBiometry(clientEntity, payload));
    }

    private Consumer<ClientsV3Entity> setCheckpointBlackList(String checkpoint) {
        return clientEntity ->
                clientEntity.getOnBoardingStatus().setCheckpoint(checkpoint);
    }

    private final Consumer<ClientsV3Entity> sendBlacklistNotification = clientEntity ->
            clientNotifyService.sendBlacklistNotification(clientEntity);

    private void rejectByBiometry(ClientsV3Entity clientEntity, ClientVerificationResult payload) {
        clientEntity.getIdentityBiometric().setTransactionState(IdentityBiometricMapper.INSTANCE.transactionStateV3From(payload.getTransactionState()));
        notifyBiometricReject(clientsOutboundAdapter)
                .andThen(updateClientEntityRepository)
                .accept(clientEntity);
    }

    private final Consumer<ClientsV3Entity> updateClientEntityRepository = clientEntity -> clientsV3Repository.save(clientEntity);

}
