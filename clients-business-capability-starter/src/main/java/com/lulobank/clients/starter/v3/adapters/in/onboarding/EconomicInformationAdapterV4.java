package com.lulobank.clients.starter.v3.adapters.in.onboarding;

import com.lulobank.clients.sdk.operations.dto.economicinformation.ClientEconomicInformation;
import com.lulobank.clients.starter.v3.handler.EconomicInformationOnboardingHandler;
import com.lulobank.clients.starter.v3.result.ClientResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v4")
public class EconomicInformationAdapterV4 {

    private final EconomicInformationOnboardingHandler economicInformationOnboardingHandler;

    public EconomicInformationAdapterV4(EconomicInformationOnboardingHandler economicInformationOnboardingHandler) {
        this.economicInformationOnboardingHandler = economicInformationOnboardingHandler;
    }

    @PostMapping(value = "/clients/{idClient}/economic-information")
    public ResponseEntity<ClientResult> saveEconomicInformation(@RequestHeader final HttpHeaders headers,
                                                                @Valid @PathVariable("idClient") @NotBlank(message = "IdClient is null or empty") String idClient,
                                                                @Valid @RequestBody ClientEconomicInformation clientEconomicInformation) {
        clientEconomicInformation.setIdClient(idClient);
        return economicInformationOnboardingHandler.saveEconomicInformation(clientEconomicInformation,headers.toSingleValueMap());
    }
}
