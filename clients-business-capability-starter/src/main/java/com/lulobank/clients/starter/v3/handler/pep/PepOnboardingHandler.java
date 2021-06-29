package com.lulobank.clients.starter.v3.handler.pep;

import com.lulobank.clients.starter.v3.result.ClientFailureResult;
import com.lulobank.clients.v3.usecase.command.UpdatePepRequest;
import com.lulobank.clients.v3.usecase.pep.UpdatePepOnboardingUseCase;
import lombok.CustomLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static com.lulobank.clients.starter.v3.result.ClientErrorCode.CLIENT_UPDATE_PEP_ERROR;

@Component
@CustomLog
public class PepOnboardingHandler {

    private final UpdatePepOnboardingUseCase updatePepOnboardingUseCase;

    @Autowired
    public PepOnboardingHandler(UpdatePepOnboardingUseCase updatePepOnboardingUseCase) {
        this.updatePepOnboardingUseCase = updatePepOnboardingUseCase;
    }

    public ResponseEntity<Object> updatePep(UpdatePepRequest updatePepRequest) {

        return updatePepOnboardingUseCase.execute(updatePepRequest)
                .peek(updatePepResponse -> log.info(String.format("PEP information was update. IdClient: %s", updatePepResponse.getIdClient())))
                .peekLeft(error -> log.error(error.getMessage()))
                .mapLeft(error -> new ClientFailureResult(CLIENT_UPDATE_PEP_ERROR, "500", "D"))
                .fold(left -> new ResponseEntity<>(left, HttpStatus.NOT_ACCEPTABLE),
                        right -> new ResponseEntity<>(right, HttpStatus.OK));
    }
}
