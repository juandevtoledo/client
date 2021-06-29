package com.lulobank.clients.v3.usecase.activateblacklistedclient;

import com.lulobank.clients.services.application.port.out.clientnotify.BlacklistStateNotifyPort;
import com.lulobank.clients.services.domain.activateblacklistedclient.ActivateBlacklistedClientRequest;
import com.lulobank.clients.v3.adapters.port.in.activateblacklistedclient.ActivateBlacklistedClientPort;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;

@CustomLog
@RequiredArgsConstructor
@AllArgsConstructor
public class ActivateBlacklistedClientUseCase implements ActivateBlacklistedClientPort {

    private final ClientsV3Repository clientsV3Repository;
    private BlacklistStateNotifyPort blacklistStateNotifyPort;

    @Override
    public Try<Void> execute(ActivateBlacklistedClientRequest command) {
        return clientsV3Repository.findByIdCard(command.getClientPersonalInformation().getDocument().getIdCard())
                .toTry()
                .peek(clientsV3Entity -> log.info("Updating client blacklisted with idCard {}",
                        command.getClientPersonalInformation().getDocument().getIdCard()))
                .flatMap(clientsV3Entity ->  updateClient(clientsV3Entity,command))
                .flatMap(clientsV3Entity -> blacklistStateNotifyPort.sendBlacklistStateNotification(clientsV3Entity));

    }

    private Try<ClientsV3Entity> updateClient(ClientsV3Entity entity, ActivateBlacklistedClientRequest command) {
        entity.setBlackListState(command.getBlacklist().getStatus().name());
        entity.setBlackListDate(command.getBlacklist().getReportDate());
        entity.setWhitelistExpirationDate(command.getWhitelistExpirationDate());
        return clientsV3Repository.updateClientBlacklisted(entity)
                .map( ___ -> entity);
    }

}
