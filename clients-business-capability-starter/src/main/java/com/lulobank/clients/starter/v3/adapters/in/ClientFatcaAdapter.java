package com.lulobank.clients.starter.v3.adapters.in;

import com.lulobank.clients.starter.adapter.in.dto.ErrorResponse;
import com.lulobank.clients.starter.adapter.in.dto.GenericResponse;
import com.lulobank.clients.starter.v3.adapters.in.dto.ClientFatcaRequest;
import com.lulobank.clients.starter.v3.handler.fatca.ClientFatcaHandler;
import io.vavr.collection.Iterator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.lulobank.clients.v3.error.ClientsDataErrorStatus.CLI_100;
import static com.lulobank.clients.v3.error.ClientsDataErrorStatus.CLI_102;
import static com.lulobank.clients.v3.error.ClientsDataErrorStatus.DEFAULT_DETAIL;
import static com.lulobank.clients.v3.error.ClientsDataErrorStatus.VALIDATION_DETAIL;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v3/client/{idClient}/fatca")
@RequiredArgsConstructor
public class ClientFatcaAdapter {

    private final ClientFatcaHandler clientFatcaHandler;

    @PostMapping
    public ResponseEntity<GenericResponse> saveInformationFatca(@RequestHeader final HttpHeaders headers,
                                                                @PathVariable("idClient") final String idClient,
                                                                @Valid @RequestBody final ClientFatcaRequest request) {
        return clientFatcaHandler.saveInformationFatca(request, idClient);
    }

    @GetMapping
    public ResponseEntity<GenericResponse> getFatcaInformation(@RequestHeader final HttpHeaders headers,
                                                               @PathVariable("idClient") final String idClient) {
        return clientFatcaHandler.getInformationFatca(idClient);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidationExceptions(MethodArgumentNotValidException ex) {
        return Iterator.ofAll(ex.getBindingResult().getAllErrors())
                .peek(error -> log.error("Validation error: {}", error.getDefaultMessage()))
                .map(error -> new ErrorResponse("400", CLI_102.name(), VALIDATION_DETAIL))
                .getOrElse(new ErrorResponse("500", CLI_100.name(), DEFAULT_DETAIL));
    }
}
