package com.lulobank.clients.starter.outboundadapter.digitalevidence;

import com.lulobank.clients.services.application.port.out.reporting.ReportingPort;
import com.lulobank.clients.services.application.port.out.reporting.model.EvidenceDocument;
import com.lulobank.clients.services.application.port.out.reporting.model.StoreDigitalEvidenceRequest;
import com.lulobank.clients.services.exception.DigitalEvidenceException;
import com.lulobank.clients.services.ports.out.DigitalEvidenceService;
import com.lulobank.clients.starter.outboundadapter.digitalevidence.mapper.DigitalEvidenceServiceMapper;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.util.DigitalEvidenceTypes;
import io.vavr.collection.Array;
import io.vavr.control.Try;

import java.util.List;
import java.util.Map;

import static com.lulobank.clients.services.application.port.out.reporting.model.EvidenceDocumentType.PRIVACY_POLICY;
import static com.lulobank.clients.services.application.port.out.reporting.model.EvidenceDocumentType.SAVINGS_ACCOUNT_CONTRACT;
import static com.lulobank.clients.services.application.port.out.reporting.model.EvidenceDocumentType.TERMS_AND_CONDITIONS;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.isIn;

public class DigitalEvidenceServiceAdapter implements DigitalEvidenceService {

    private static final String ERROR_SAVING_EVIDENCE = "Error saving digital evidence for client %s";
    private final ReportingPort digitalEvidence;

    public DigitalEvidenceServiceAdapter(ReportingPort digitalEvidence) {
        this.digitalEvidence = digitalEvidence;
    }

    @Override
    public Try<Boolean> saveDigitalEvidence(Map<String, String> headers, ClientsV3Entity client, DigitalEvidenceTypes digitalEvidenceTypes) {
        StoreDigitalEvidenceRequest request = Match(digitalEvidenceTypes).of(
                Case($(isIn(DigitalEvidenceTypes.APP)), ()-> getStoreDigitalEvidenceApp(client)),
                Case($(isIn(DigitalEvidenceTypes.SAVINGS_ACCOUNT)), ()-> getStoreDigitalEvidenceSavingAccount(client)),
                Case($(), StoreDigitalEvidenceRequest::new));
        return digitalEvidence.createDigitalEvidence(headers, client.getIdClient(), request)
                .map(r -> true)
                .orElse(Try.failure(new DigitalEvidenceException(String.format(ERROR_SAVING_EVIDENCE, client.getIdClient()))));
    }

    private StoreDigitalEvidenceRequest getStoreDigitalEvidenceApp(ClientsV3Entity client) {
        StoreDigitalEvidenceRequest request = DigitalEvidenceServiceMapper.INSTANCE.toStoreDigitalEvidenceRequest(client);
        List<EvidenceDocument> evidenceDocumentList = Array.of(PRIVACY_POLICY, TERMS_AND_CONDITIONS)
                .map(EvidenceDocument::new).asJava();
        request.setDocumentsToStore(evidenceDocumentList);
        return request;
    }

    private StoreDigitalEvidenceRequest getStoreDigitalEvidenceSavingAccount(ClientsV3Entity client) {
        StoreDigitalEvidenceRequest request = DigitalEvidenceServiceMapper.INSTANCE.toStoreDigitalEvidenceRequest(client);
        List<EvidenceDocument> evidenceDocumentList = Array.of(SAVINGS_ACCOUNT_CONTRACT)
                .map(EvidenceDocument::new).asJava();
        request.setDocumentsToStore(evidenceDocumentList);
        return request;
    }

}
