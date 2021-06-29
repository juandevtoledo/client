package com.lulobank.clients.starter.outboundadapter.digitalevidence;

import co.com.lulobank.tracing.restTemplate.RestTemplateClient;
import com.lulobank.clients.services.exception.DigitalEvidenceException;
import com.lulobank.clients.services.ports.out.AcceptancesDocumentService;
import com.lulobank.reporting.sdk.operations.dto.AcceptancesDocument;
import io.vavr.control.Try;
import lombok.CustomLog;

import java.util.Map;
@CustomLog
public class AcceptancesDocumentServiceAdapter implements AcceptancesDocumentService {

    private static final String ERROR_ACCEPTANCES_DOCUMENT = "Error saving digital evidence for client %s";
    private static final String SUCCESS_ACCEPTANCES_DOCUMENT = "Digital evidence in reporting created for client %s";
    private static final String ACCEPTANCES_DOCUMENTS_SERVICE_ENDPOINT ="reporting/clients/%s/app-acceptances";
    private final RestTemplateClient restTemplateClient;

    public AcceptancesDocumentServiceAdapter(RestTemplateClient restTemplateClient) {
        this.restTemplateClient = restTemplateClient;
    }

    @Override
    public Try<Boolean> generateAcceptancesDocument(Map<String, String> headers, String idClient,
                                                    AcceptancesDocument acceptancesDocument) {
        String context = String.format(ACCEPTANCES_DOCUMENTS_SERVICE_ENDPOINT, idClient);
        return restTemplateClient.post(context, acceptancesDocument, headers,null)
                .peek(re-> log.info(String.format(SUCCESS_ACCEPTANCES_DOCUMENT,idClient)))
                .peekLeft(error -> log.error(String.format(ERROR_ACCEPTANCES_DOCUMENT,idClient), error.getBody()))
                .map(r->true)
                .toTry(()-> new DigitalEvidenceException(String.format(ERROR_ACCEPTANCES_DOCUMENT,idClient)))
                ;
    }
}
