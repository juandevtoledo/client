package com.lulobank.clients.starter.v3.adapters.in.pep;

import com.lulobank.clients.starter.v3.handler.pep.PepOnboardingHandler;
import com.lulobank.clients.v3.usecase.command.UpdatePepRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v4")
public class PepAdapterV4 {

    private PepOnboardingHandler pepOnboardingHandler;

    @Autowired
    public PepAdapterV4(PepOnboardingHandler pepOnboardingHandler) {
        this.pepOnboardingHandler = pepOnboardingHandler;
    }

    @PostMapping(value = "/clients/{idClient}/pep")
    public ResponseEntity<Object> updatePepClient(@RequestHeader final HttpHeaders headers,
                                                  @Valid @PathVariable("idClient") @NotBlank(message = "IdClient is null or empty") String idClient,
                                                  @RequestBody UpdatePepRequest updatePepRequest) {
        updatePepRequest.setIdClient(idClient);
        return pepOnboardingHandler.updatePep(updatePepRequest);
    }

}
