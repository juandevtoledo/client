package com.lulobank.clients.starter.v3.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.lulobank.clients.starter.v3.result.ClientFailureResult;
import com.lulobank.clients.starter.v3.result.ClientResult;
import com.lulobank.clients.starter.v3.result.ClientSuccessResult;
import com.lulobank.clients.v3.usecase.ClientsDemographicUseCase;

import lombok.CustomLog;

import static com.lulobank.clients.starter.v3.result.ClientErrorCode.CLIENT_DEMOGRAPHIC_NOT_FOUND_ERROR;

@Component
@CustomLog
public class ClientsDemographicHandler {

    private final ClientsDemographicUseCase clientsDemographicUseCase;

    @Autowired
    public ClientsDemographicHandler(ClientsDemographicUseCase clientsDemographicUseCase) {
        this.clientsDemographicUseCase = clientsDemographicUseCase;
    }

    public ResponseEntity<ClientResult> getDemographicInfoByClient(String idClient) {

        return clientsDemographicUseCase.execute(idClient)
                .peek(clientDemographicInfo -> log.info("Client Demographic info was found"))
                .map(ClientSuccessResult::new)
                .peekLeft(error -> log.error(error.getMessage()))
                .mapLeft(error -> mapError())
                .fold(left -> new ResponseEntity<>(left, HttpStatus.NOT_FOUND),
                        right -> new ResponseEntity<>(right, HttpStatus.OK));
    }

    private ClientFailureResult mapError() {
        return new ClientFailureResult(CLIENT_DEMOGRAPHIC_NOT_FOUND_ERROR, "404", "D");
    }
}
