package com.lulobank.clients.starter.v3.adapters.in.onboarding;

import com.lulobank.clients.starter.adapter.in.dto.GenericResponse;
import com.lulobank.clients.starter.v3.handler.AccountSettlementHandler;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/v3")
public class AccountSettlementAdapter {

    private final AccountSettlementHandler accountSettlementHandler;

    public AccountSettlementAdapter(AccountSettlementHandler accountSettlementHandler) {
        this.accountSettlementHandler = accountSettlementHandler;
    }

    @PostMapping(value = "/clients/{idClient}/account-settlement")
    public ResponseEntity<GenericResponse> processAccountSettlement(@RequestHeader final HttpHeaders headers,
                                                                    @Valid @PathVariable("idClient") @NotBlank(message = "IdClient is null or empty") String idClient) {
        return accountSettlementHandler.processAccountSettlement(idClient);
    }
}
