package com.lulobank.clients.starter.inboundadapter;

import com.lulobank.clients.services.exception.DigitalEvidenceException;
import com.lulobank.clients.services.inboundadapters.model.ClientsFailureResult;
import com.lulobank.clients.services.inboundadapters.model.ClientsResult;
import com.lulobank.clients.services.usecase.AcceptancesDocumentUseCase;
import com.lulobank.clients.services.usecase.command.SaveAcceptancesDocument;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.lulobank.clients.services.utils.ClientsErrorResponse.DIGITAL_EVIDENCE_ERROR;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/")
@Slf4j
public class AcceptancesDocumentAdapter {

    private static final String ERROR_UNKNOWN = "An error was produced while checking acceptances document. Cause: {}";
    private static final String ERROR_SAVING_ACCEPTANCES_DOCUMENT = "Error saving acceptances document. Cause: {}";
    private static final String ACCEPTANCES_DOCUMENT_FAILURE = "500";

    private final AcceptancesDocumentUseCase acceptancesDocumentUseCase;

    public AcceptancesDocumentAdapter(AcceptancesDocumentUseCase acceptancesDocumentUseCase) {
        this.acceptancesDocumentUseCase = acceptancesDocumentUseCase;
    }

    @PutMapping("{idClient}/app-acceptances")
    public ResponseEntity<ClientsResult> saveAcceptancesDocument(@RequestHeader final HttpHeaders headers,
                                                                @PathVariable("idClient") final String idClient){
        SaveAcceptancesDocument saveAcceptancesDocument = new SaveAcceptancesDocument();
        saveAcceptancesDocument.setIdClient(idClient);
        saveAcceptancesDocument.setHttpHeaders(headers.toSingleValueMap());

        return acceptancesDocumentUseCase.execute(saveAcceptancesDocument)
                .map(response -> new ResponseEntity<ClientsResult>(HttpStatus.OK))
                .recover(DigitalEvidenceException.class, this::digitalEvidenceError)
                .onFailure(e -> log.error(ERROR_UNKNOWN, e.getMessage(), e))
                .recover(e -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ClientsFailureResult()))
                .get();
    }

    private ResponseEntity<ClientsResult> digitalEvidenceError(DigitalEvidenceException dee) {
        log.error(ERROR_SAVING_ACCEPTANCES_DOCUMENT, dee.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ClientsFailureResult()
                        .setCode(DIGITAL_EVIDENCE_ERROR.code())
                        .setFailure(ACCEPTANCES_DOCUMENT_FAILURE)
                        .setDetail("U")
                );
    }
}
