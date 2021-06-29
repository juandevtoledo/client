package com.lulobank.clients.starter.v3.adapters.in.fatca;

import com.lulobank.clients.starter.adapter.in.dto.GenericResponse;
import com.lulobank.clients.starter.v3.adapters.in.dto.ClientFatcaRequest;
import com.lulobank.clients.starter.v3.handler.fatca.ClientFatcaOnboardingHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v4/clients/{idClient}/fatca")
@RequiredArgsConstructor
public class ClientFatcaOnboardingAdapter {

    private final ClientFatcaOnboardingHandler clientFatcaOnboardingHandler;

    @PostMapping
    public ResponseEntity<GenericResponse> saveInformationFatca(@RequestHeader final HttpHeaders headers,
                                                                @PathVariable("idClient") final String idClient,
                                                                @Valid @RequestBody final ClientFatcaRequest request) {
        return clientFatcaOnboardingHandler.saveInformationFatca(request, idClient);
    }
}
