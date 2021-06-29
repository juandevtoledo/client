package com.lulobank.clients.starter.v3.handler;

import com.lulobank.clients.sdk.operations.AdapterCredentials;
import com.lulobank.clients.sdk.operations.dto.economicinformation.ClientEconomicInformation;
import com.lulobank.clients.starter.v3.result.ClientFailureResult;
import com.lulobank.clients.starter.v3.result.ClientResult;
import com.lulobank.clients.v3.usecase.economicinformation.SaveEconomicInformationInOnboardingUseCase;
import lombok.CustomLog;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.lulobank.clients.starter.v3.result.ClientErrorCode.CLIENT_SAVE_ECONOMIC_INFORMATION_ERROR;

@Component
@CustomLog
public class EconomicInformationOnboardingHandler {

    private final SaveEconomicInformationInOnboardingUseCase saveEconomicInformationInOnboardingUseCase;

    public EconomicInformationOnboardingHandler(SaveEconomicInformationInOnboardingUseCase saveEconomicInformationInOnboardingUseCase) {
        this.saveEconomicInformationInOnboardingUseCase = saveEconomicInformationInOnboardingUseCase;
    }

    public ResponseEntity<ClientResult> saveEconomicInformation(ClientEconomicInformation clientEconomicInformation, Map<String,String> header){
        return saveEconomicInformationInOnboardingUseCase.execute(setCredentials(clientEconomicInformation,header))
                .peek(clientsBiometric -> log.info("Economic information of onboarding was saved"))
                .peekLeft(error -> log.error(error.getMessage()))
                .mapLeft(error -> mapError())
                .fold(left -> new ResponseEntity<>(left, HttpStatus.NOT_ACCEPTABLE),
                        right -> new ResponseEntity<>(HttpStatus.CREATED));
    }

    private ClientFailureResult mapError() {
        return new ClientFailureResult(CLIENT_SAVE_ECONOMIC_INFORMATION_ERROR, "406", "D");
    }

    private ClientEconomicInformation setCredentials(ClientEconomicInformation clientEconomicInformation, Map<String,String> header){
        clientEconomicInformation.setAdapterCredentials(new AdapterCredentials(header));
        return clientEconomicInformation;
    }
}
