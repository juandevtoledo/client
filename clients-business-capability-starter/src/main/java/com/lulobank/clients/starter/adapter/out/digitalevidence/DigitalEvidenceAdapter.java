package com.lulobank.clients.starter.adapter.out.digitalevidence;

import co.com.lulobank.tracing.restTemplate.RestTemplateClient;
import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.starter.adapter.out.digitalevidence.dto.AppAcceptanceDocumentsResponse;
import com.lulobank.clients.starter.adapter.out.digitalevidence.dto.DigitalEvidenceResponse;
import com.lulobank.clients.v3.adapters.port.out.digitalevidence.DigitalEvidenceError;
import com.lulobank.clients.v3.adapters.port.out.digitalevidence.DigitalEvidenceServicePort;
import com.lulobank.clients.v3.adapters.port.out.digitalevidence.dto.AcceptancesDocumentRequest;
import com.lulobank.clients.v3.adapters.port.out.digitalevidence.dto.AppAcceptanceDocuments;
import com.lulobank.clients.v3.adapters.port.out.digitalevidence.dto.DigitalEvidenceDocuments;
import com.lulobank.clients.v3.adapters.port.out.digitalevidence.dto.DigitalEvidenceRequest;
import io.vavr.control.Either;
import lombok.CustomLog;
import org.springframework.http.ResponseEntity;

import java.text.MessageFormat;
import java.util.Map;

@CustomLog
public class DigitalEvidenceAdapter implements DigitalEvidenceServicePort {

    private static final String SAVE_DIGITAL_EVIDENCE_RESOURCE = "digital-evidence-xbc/v2/clients/%s/digital-evidence";
    private static final String CREATE_ACCEPTANCES_DOCUMENT = "digital-evidence-xbc/v2/clients/%s/app-acceptances";

    private final RestTemplateClient digitalEvidenceRestTemplateClient;

    public DigitalEvidenceAdapter(RestTemplateClient digitalEvidenceRestTemplateClient) {
        this.digitalEvidenceRestTemplateClient = digitalEvidenceRestTemplateClient;
    }

    @Override
    public Either<UseCaseResponseError, DigitalEvidenceDocuments> saveDigitalEvidence(Map<String, String> headers,
                                                                                      String idClient,
                                                                                      DigitalEvidenceRequest digitalEvidenceRequest) {
        String context = String.format(SAVE_DIGITAL_EVIDENCE_RESOURCE, idClient);
        return digitalEvidenceRestTemplateClient.post(context, digitalEvidenceRequest, headers, DigitalEvidenceResponse.class)
                .peekLeft(error -> log.error("Error consuming service for digital evidence creation. Endpoint clients/%s/digital-evidence. Cause: %s",
                        idClient, error.getBody()))
                .mapLeft(error -> DigitalEvidenceError.unknownError())
                .map(ResponseEntity::getBody)
                .map(digitalEvidenceResponse -> DigitalEvidenceDocuments.builder()
                        .response(digitalEvidenceResponse.getResponse()).build())
                .mapLeft(UseCaseResponseError::map);
    }

    @Override
    public Either<UseCaseResponseError, AppAcceptanceDocuments> createAcceptancesDocument(Map<String, String> headers,
                                                                                          String idClient,
                                                                                          AcceptancesDocumentRequest acceptancesDocumentRequest) {

        log.info(MessageFormat.format("Creating acceptances documents for {0} ", idClient));
        String context = String.format(CREATE_ACCEPTANCES_DOCUMENT, idClient);
        return digitalEvidenceRestTemplateClient.post(context, acceptancesDocumentRequest, headers, AppAcceptanceDocumentsResponse.class)
                .peek(res-> log.info(MessageFormat.format("Acceptances documents created successful {0}} ",idClient)))
                .peekLeft(error ->
                        log.error(MessageFormat.format("Error consuming acceptance document creation service, Endpoint clients/%s/app-acceptances. Code {0} Cause: {1}}",
                                idClient, error.getStatusCode(), error.getBody())))
                .mapLeft(error -> DigitalEvidenceError.unknownError())
                .map(ResponseEntity::getBody)
                .map(appAcceptanceDocumentsResponse -> AppAcceptanceDocuments.builder()
                        .response(appAcceptanceDocumentsResponse.getResponse()).build())
                .mapLeft(UseCaseResponseError::map);
    }
}
