package com.lulobank.clients.starter.v3.adapters.in;

import com.lulobank.clients.starter.v3.adapters.in.dto.ChangeProductResponse;
import com.lulobank.clients.starter.v3.adapters.in.dto.ErrorResult;
import com.lulobank.clients.v3.usecase.ChangeProductSavingUseCase;
import com.lulobank.clients.v3.usecase.command.ChangeProductResponseError;
import com.lulobank.clients.v3.usecase.command.ChangeProductSaving;
import com.lulobank.clients.v3.vo.AdapterCredentials;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/v3/onboarding")
@RequiredArgsConstructor
public class ClientOnboardingAdapterV3 {

    private ChangeProductSavingUseCase changeProductSavingUseCase;

    @Autowired
    public ClientOnboardingAdapterV3(ChangeProductSavingUseCase changeProductSavingUseCase) {
        this.changeProductSavingUseCase = changeProductSavingUseCase;
    }

    @PostMapping(value = "/client/{idClient}/change/product/savings")
    public ResponseEntity<ChangeProductResponse> changingProductToSaving(@RequestHeader final HttpHeaders headers,
                                                                       @Valid @PathVariable("idClient") @NotBlank(message = "IdClient is null or empty") String idClient
    ) {
        return changeProductSavingUseCase.execute(new ChangeProductSaving(idClient,new AdapterCredentials(headers.toSingleValueMap())))
                .fold(this::errorResponse, success -> new ResponseEntity<>(HttpStatus.CREATED));
    }

    public ResponseEntity<ChangeProductResponse> errorResponse(ChangeProductResponseError error) {
        ChangeProductResponse changeProductSaving = new ChangeProductResponse(new ErrorResult(error.getMessage(), HttpStatus.BAD_GATEWAY.value(), error.getCode()));
        return new ResponseEntity<>(changeProductSaving, HttpStatus.BAD_GATEWAY);
    }

}
