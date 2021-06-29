package com.lulobank.clients.v3.usecase.activateselfcertifiedpepclient;

import com.lulobank.clients.sdk.operations.util.CheckPoints;
import com.lulobank.clients.services.domain.activateselfcertifiedpepclient.ActivatePepClientRequest;
import com.lulobank.clients.services.utils.DatesUtil;
import com.lulobank.clients.v3.adapters.port.in.activateselfcertifiedpepclient.ActivatePepClientPort;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.adapters.port.out.notification.ActivatePepNotifyPort;
import com.lulobank.clients.v3.usecase.pep.PepStatus;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.owasp.encoder.Encode;

@CustomLog
@RequiredArgsConstructor
public class ActivatePepClientUseCase implements ActivatePepClientPort {

    private final ClientsV3Repository clientsV3Repository;
    private final ActivatePepNotifyPort activatePepNotifyPort;

    @Override
    public Try<Void> execute(ActivatePepClientRequest command) {
        return clientsV3Repository.findByIdCard(command.getClientPersonalInformation().getDocument().getCardId())
                .toTry()
                .peek(clientsV3Entity -> log.info("Updating client PEP with idCard {}",
                        command.getClientPersonalInformation().getDocument().getCardId()))
                .flatMap(clientsV3Entity -> processUpdate(clientsV3Entity, command))
                .flatMap(this::sendNotification);
    }

    private Try<Void> sendNotification(ClientsV3Entity clientsV3Entity) {
        return Option.of(clientsV3Entity)
                .filter(entity -> PepStatus.PEP_WHITELISTED.value().equals(entity.getPep()))
                .map(activatePepNotifyPort::sendActivatePepNotification)
                .getOrElse(() -> Try.run(() -> log.info("Client with idCard {} PEP blacklisted",clientsV3Entity.getIdCard())));
    }

    private Try<ClientsV3Entity> processUpdate(ClientsV3Entity clientsV3Entity, ActivatePepClientRequest command) {
        clientsV3Entity.setPep(command.isWhitelisted() ? PepStatus.PEP_WHITELISTED.value() : PepStatus.PEP_BLACKLISTED.value());
        clientsV3Entity.setDateResponsePep(DatesUtil.getLocalDateGMT5());
        clientsV3Entity.getOnBoardingStatus().setCheckpoint(CheckPoints.PEP_FINISHED.name());
        log.info("Set checkpoint : {},  idClient {}", Encode.forJava(CheckPoints.PEP_FINISHED.name()),
                Encode.forJava(clientsV3Entity.getIdClient()));
        return clientsV3Repository.save(clientsV3Entity)
                .map(r -> clientsV3Entity);
    }
}
