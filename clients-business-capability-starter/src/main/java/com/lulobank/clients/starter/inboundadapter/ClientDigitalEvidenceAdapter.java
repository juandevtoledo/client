package com.lulobank.clients.starter.inboundadapter;

import com.lulobank.clients.services.exception.DigitalEvidenceException;
import com.lulobank.clients.services.exception.TimestampDigitalEvidenceException;
import com.lulobank.clients.services.inboundadapters.model.ClientsFailureResult;
import com.lulobank.clients.services.inboundadapters.model.ClientsResult;
import com.lulobank.clients.services.usecase.ClientDigitalEvidenceUseCase;
import com.lulobank.clients.services.usecase.command.SaveDigitalEvidence;
import com.lulobank.clients.v3.util.DigitalEvidenceTypes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.lulobank.clients.services.utils.ClientsErrorResponse.DIGITAL_EVIDENCE_ERROR;
import static com.lulobank.clients.services.utils.ClientsErrorResponse.TIMESTAMP_ERROR;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/")
@Slf4j
public class ClientDigitalEvidenceAdapter {

    private static final String ERROR_UNKNOWN = "An error was produced while checking digital evidence. Cause: {}";
    private static final String ERROR_TIMESTAMP = "Error processing documents acceptance timestamp. Cause: {}";
    private static final String ERROR_SAVING_DIGITAL_EVIDENCE = "Error saving digital evidence. Cause: {}";
    private static final String DIGITAL_EVIDENCE_FAILURE = "500";

    private final ClientDigitalEvidenceUseCase clientDigitalEvidenceUseCase;

    public ClientDigitalEvidenceAdapter(ClientDigitalEvidenceUseCase clientDigitalEvidenceUseCase) {
        this.clientDigitalEvidenceUseCase = clientDigitalEvidenceUseCase;
    }

    @PostMapping(value = "{idClient}/digital-evidence", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClientsResult> saveDigitalEvidence(@RequestHeader final HttpHeaders headers,
                                                             @PathVariable("idClient") final String idClient,
                                                             @RequestBody DigitalEvidenceTypes digitalEvidenceTypes) {

        SaveDigitalEvidence saveDigitalEvidence = new SaveDigitalEvidence();
        saveDigitalEvidence.setIdClient(idClient);
        saveDigitalEvidence.setDigitalEvidenceTypes(digitalEvidenceTypes);
        saveDigitalEvidence.setHttpHeaders(headers.toSingleValueMap());

        return clientDigitalEvidenceUseCase.execute(saveDigitalEvidence)
                .map(response -> new ResponseEntity<ClientsResult>(HttpStatus.OK))
                .recover(DigitalEvidenceException.class, this::digitalEvidenceError)
                .recover(TimestampDigitalEvidenceException.class, this::timestampError)
                .onFailure(e -> log.error(ERROR_UNKNOWN, e.getMessage(), e))
                .recover(e -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ClientsFailureResult()))
                .get();
    }

    private ResponseEntity<ClientsResult> digitalEvidenceError(DigitalEvidenceException dee) {
        log.error(ERROR_SAVING_DIGITAL_EVIDENCE, dee.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ClientsFailureResult()
                                .setCode(DIGITAL_EVIDENCE_ERROR.code())
                                .setFailure(DIGITAL_EVIDENCE_FAILURE)
                                .setDetail("U")
                        );
    }

    private ResponseEntity<ClientsResult> timestampError(TimestampDigitalEvidenceException tse) {
        log.error(ERROR_TIMESTAMP, tse.getMessage(), tse);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ClientsFailureResult()
                                .setCode(TIMESTAMP_ERROR.code())
                                .setFailure(TIMESTAMP_ERROR.name())
                                .setDetail(tse.getMessage())
                        );
    }

}
