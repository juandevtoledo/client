package com.lulobank.clients.services.features.clientverificationresult;

import com.lulobank.clients.services.events.ClientVerificationResult;
import com.lulobank.clients.services.features.RetriesOption;
import com.lulobank.clients.services.features.clientverificationresult.mapper.EconomicInformationMapper;
import com.lulobank.clients.services.features.clientverificationresult.mapper.IdentityInformationMapper;
import com.lulobank.clients.services.outboundadapters.ClientsOutboundAdapter;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessagingException;

import java.util.Map;
import java.util.function.Consumer;

import static com.lulobank.clients.services.features.clientverificationresult.MessageSqsHelper.sendToSqs;
import static com.lulobank.clients.services.features.clientverificationresult.NotificationHelper.notifyErrorSendingRisk;
import static com.lulobank.clients.services.utils.SQSUtil.retryEvent;

@Slf4j
public class ProcessResultByCredit extends ProcessResultByType {

    private final ClientsOutboundAdapter clientsOutboundAdapter;
    private final ClientsV3Entity clientEntity;
    private final RetriesOption retriesOption;
    private final Map<String, Object> headersSqs;
    private final ClientVerificationResult payload;
    private ClientsV3Repository clientsV3Repository;

    public ProcessResultByCredit(ClientsOutboundAdapter clientsOutboundAdapter,
                                 ClientsV3Entity clientEntity,
                                 RetriesOption retriesOption,
                                 Map<String, Object> headersSqs,
                                 ClientVerificationResult payload,
                                 ClientsV3Repository clientsV3Repository) {
        this.clientsOutboundAdapter = clientsOutboundAdapter;
        this.clientEntity = clientEntity;
        this.retriesOption = retriesOption;
        this.headersSqs = headersSqs;
        this.payload = payload;
        this.clientsV3Repository = clientsV3Repository;
    }

    @Override
    public void process() {
        Try.of(this::sendEventToRisk)
                .peek(update)
                .onSuccess(success -> log.info("Onboarding CreditsAccount was update by idClient : {}", clientEntity.getIdClient()))
                .onFailure(MessagingException.class, ex -> handleMessagingException(payload, clientEntity, ex));
    }

    private ClientsV3Entity sendEventToRisk() {
        return Try.of(() -> clientEntity)
                .peek(entity -> log.info("Validating flags to send risk events: identityProcessed = {} , economicProcessed = {}",
                        entity.isIdentityProcessed(), entity.isEconomicProcessed()))
                .filter(ClientsV3Entity::isEconomicProcessed)
                .map(EconomicInformationMapper.INSTANCE::economicInformationFromEntity)
                .peek(economicInfo -> sendToSqs(clientsOutboundAdapter, economicInfo))
                .map(economicInfo -> IdentityInformationMapper.INSTANCE.identityInformationFromClient(clientEntity))
                .peek(identityInfo -> sendToSqs(clientEntity, identityInfo, clientsOutboundAdapter))
                .peek(identityInfo -> log.info("Event sent to risk engine for idClient: {}", clientEntity.getIdClient()))
                .map(identityInfo -> clientEntity)
                .getOrElse(clientEntity);
    }


    private final Consumer<ClientsV3Entity> update = entity -> clientsV3Repository.updateOnBoarding(entity);

    private void handleMessagingException(ClientVerificationResult payload, ClientsV3Entity clientEntity, MessagingException ex) {
        log.error("Error while sending message to risk engine for IdTransactionBiometric: {}, IdClient: {}", payload.getIdTransactionBiometric(), clientEntity.getIdClient(), ex);
        if (retryEvent(payload, headersSqs, retriesOption)) {
            sendToSqs(payload, clientsOutboundAdapter);
        } else {
            notifyErrorSendingRisk(clientsOutboundAdapter, clientEntity);
        }
    }
}
