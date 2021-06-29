package com.lulobank.clients.services.usecase;

import com.lulobank.clients.services.application.port.in.UseCase;
import com.lulobank.clients.services.ports.out.DigitalEvidenceService;
import com.lulobank.clients.services.usecase.command.SaveDigitalEvidence;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.util.DigitalEvidenceTypes;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.isIn;

@Slf4j
public class ClientDigitalEvidenceUseCase implements UseCase<SaveDigitalEvidence, Try<Boolean>> {

    private final DigitalEvidenceService digitalEvidenceService;
    private final ClientsV3Repository clientsV3Repository;

    public ClientDigitalEvidenceUseCase(DigitalEvidenceService digitalEvidenceService,
                                        ClientsV3Repository clientsV3Repository) {
        this.digitalEvidenceService = digitalEvidenceService;
        this.clientsV3Repository = clientsV3Repository;
    }

    @Override
    public Try<Boolean> execute(SaveDigitalEvidence command) {
        return clientsV3Repository.findByIdClient(command.getIdClient()).toTry()
                .flatMap(client -> saveDigitalEvidence(command, client));
    }

    private Try<Boolean> saveDigitalEvidence(SaveDigitalEvidence command, ClientsV3Entity client) {
        return Match(command.getDigitalEvidenceTypes()).of(
                Case($(isIn(DigitalEvidenceTypes.APP)), ()-> saveDigitalEvidenceApp(command, client)),
                Case($(isIn(DigitalEvidenceTypes.SAVINGS_ACCOUNT)), ()-> saveDigitalEvidenceSavingAccount(command, client)));
    }

    private Try<Boolean> saveDigitalEvidenceApp(SaveDigitalEvidence command, ClientsV3Entity client){
        return digitalEvidenceService.saveDigitalEvidence(command.getAuthorizationHeader(), client, command.getDigitalEvidenceTypes())
                .peek(r -> client.setDigitalStorageStatus(true))
                .peek(r -> clientsV3Repository.save(client));
    }

    private Try<Boolean> saveDigitalEvidenceSavingAccount(SaveDigitalEvidence command, ClientsV3Entity client){
        return digitalEvidenceService.saveDigitalEvidence(command.getAuthorizationHeader(), client, command.getDigitalEvidenceTypes())
                .peek(r -> client.setCatsDocumentStatus(true))
                .peek(r -> clientsV3Repository.save(client));
    }

}
