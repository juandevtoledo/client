package com.lulobank.clients.starter.v3.handler.pep;

import static com.lulobank.clients.starter.v3.result.ClientErrorCode.CLIENT_RETRIEVED_PEP_ERROR;
import static com.lulobank.clients.starter.v3.result.ClientErrorCode.CLIENT_UPDATE_PEP_ERROR;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.lulobank.clients.starter.v3.result.ClientFailureResult;
import com.lulobank.clients.v3.usecase.pep.GetPepUseCase;
import com.lulobank.clients.v3.usecase.pep.UpdatePepUseCase;
import com.lulobank.clients.v3.usecase.command.UpdatePepRequest;

import lombok.CustomLog;

@Component
@CustomLog
public class PepHandler {

    private final UpdatePepUseCase updatePepUseCase;
    private final GetPepUseCase getPepUseCase;

    @Autowired
    public PepHandler(UpdatePepUseCase updatePepUseCase, GetPepUseCase getPepUseCase) {
        this.updatePepUseCase = updatePepUseCase;
        this.getPepUseCase = getPepUseCase;
    }

    public ResponseEntity<Object> updatePep(UpdatePepRequest updatePepRequest) {

        return updatePepUseCase.execute(updatePepRequest)
                .peek(updatePepResponse -> log.info(String.format("PEP information was update. IdClient: %s", updatePepResponse.getIdClient())))
                .peekLeft(error -> log.error(error.getMessage()))
                .mapLeft(error -> new ClientFailureResult(CLIENT_UPDATE_PEP_ERROR, "500", "D"))
                .fold(left -> new ResponseEntity<>(left, HttpStatus.NOT_ACCEPTABLE),
                        right -> new ResponseEntity<>(right, HttpStatus.OK));
    }
    
    public ResponseEntity<Object> getPep(String idClient) {

        return getPepUseCase.execute(idClient)
                .peek(getPepResponse -> log.info(String.format("PEP information retrived. IdClient: %s", idClient)))
                .peekLeft(error -> log.error(error.getMessage()))
                .mapLeft(error -> new ClientFailureResult(CLIENT_RETRIEVED_PEP_ERROR, "500", "D"))
                .fold(left -> new ResponseEntity<>(left, HttpStatus.NOT_ACCEPTABLE),
                        right -> new ResponseEntity<>(right, HttpStatus.OK));
    }

}
