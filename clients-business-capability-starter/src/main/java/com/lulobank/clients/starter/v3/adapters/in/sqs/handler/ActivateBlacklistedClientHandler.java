package com.lulobank.clients.starter.v3.adapters.in.sqs.handler;

import com.lulobank.clients.services.domain.StateBlackList;
import com.lulobank.clients.services.domain.activateblacklistedclient.ActivateBlacklistedClientRequest;
import com.lulobank.clients.services.domain.activateblacklistedclient.Blacklist;
import com.lulobank.clients.services.domain.activateblacklistedclient.ClientPersonalInformation;
import com.lulobank.clients.services.domain.activateblacklistedclient.Document;
import com.lulobank.clients.v3.adapters.port.in.activateblacklistedclient.ActivateBlacklistedClientPort;
import com.lulobank.clients.starter.v3.adapters.in.sqs.event.ActivateBlacklistedClient;
import com.lulobank.events.api.EventHandler;
import io.vavr.control.Try;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

@CustomLog
@RequiredArgsConstructor
public class ActivateBlacklistedClientHandler implements EventHandler<ActivateBlacklistedClient> {

    private final ActivateBlacklistedClientPort activateBlacklistedClientPort;

    @Override
    public Try<Void> execute(ActivateBlacklistedClient event) {
        return Try.of(() -> buildRequest(event))
                .flatMap(activateBlacklistedClientPort::execute)
                .onSuccess(success -> log.info("ActivateBlacklistedClient Event was successful process , idCard {} ",
                        event.getClientPersonalInformation().getIdDocument().getIdCard()))
                .onFailure(error -> log.info("ActivateBlacklistedClient Event was failed , idCard {}, msg : {} ",
                        event.getClientPersonalInformation().getIdDocument().getIdCard(), error.getMessage(), error));

    }

    private ActivateBlacklistedClientRequest buildRequest(ActivateBlacklistedClient event) {
        return ActivateBlacklistedClientRequest.builder()
                .idTransactionBiometric(event.getIdTransactionBiometric())
                .whitelistExpirationDate(StringUtils.isEmpty(event.getWhitelistExpirationDate()) ? null :
                        LocalDateTime.parse(event.getWhitelistExpirationDate()))
                .clientPersonalInformation(ClientPersonalInformation.builder()
                        .document(Document.builder()
                                .documentType(event.getClientPersonalInformation().getIdDocument().getDocumentType())
                                .idCard(event.getClientPersonalInformation().getIdDocument().getIdCard())
                                .expeditionDate(event.getClientPersonalInformation().getIdDocument().getExpeditionDate())
                                .build())
                        .build())
                .blacklist(Blacklist.builder()
                        .status(StateBlackList.valueOf(event.getBlacklist().getStatus()))
                        .reportDate(LocalDateTime.parse(event.getBlacklist().getReportDate()))
                        .riskLevel(event.getBlacklist().getRiskLevel())
                        .build())
                .build();
    }

    @Override
    public Class<ActivateBlacklistedClient> eventClass() {
        return ActivateBlacklistedClient.class;
    }
}
