package com.lulobank.clients.services.usecase;

import com.lulobank.clients.services.application.port.in.UseCase;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.ports.out.AcceptancesDocumentService;
import com.lulobank.clients.services.ports.repository.ClientsRepositoryV2;
import com.lulobank.clients.services.usecase.command.SaveAcceptancesDocument;
import com.lulobank.clients.services.utils.DatesUtil;
import com.lulobank.reporting.sdk.operations.dto.AcceptancesDocument;
import io.vavr.control.Try;

import java.util.Map;

public class AcceptancesDocumentUseCase implements UseCase<SaveAcceptancesDocument, Try<Boolean>> {

    private final AcceptancesDocumentService acceptancesDocumentService;
    private final ClientsRepositoryV2 clientsRepository;

    public AcceptancesDocumentUseCase(AcceptancesDocumentService acceptancesDocumentService, ClientsRepositoryV2 clientsRepository) {
        this.acceptancesDocumentService = acceptancesDocumentService;
        this.clientsRepository = clientsRepository;
    }

    @Override
    public Try<Boolean> execute(SaveAcceptancesDocument command) {
        return clientsRepository.findByIdClient(command.getIdClient()).toTry()
                .flatMap(client -> saveAcceptancesDocument(command.getAuthorizationHeader(), client));
    }

    private Try<Boolean> saveAcceptancesDocument(Map<String, String> headers, ClientEntity clientEntity){
        AcceptancesDocument acceptancesDocument = new AcceptancesDocument();
        acceptancesDocument.setDocumentAcceptancesTimestamp(DatesUtil.getFormattedDate(clientEntity.getAcceptances()
                .getDocumentAcceptancesTimestamp()));
        return acceptancesDocumentService.generateAcceptancesDocument(headers, clientEntity.getIdClient(), acceptancesDocument)
                .peek(response-> clientEntity.getAcceptances().setPersistedInDigitalEvidence(true))
                .peek(response-> clientsRepository.save(clientEntity));
    }
}
