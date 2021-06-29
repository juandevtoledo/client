package com.lulobank.clients.starter.v3.adapters.in.updateemailaddress;

import com.lulobank.clients.starter.adapter.in.dto.ErrorResponse;
import com.lulobank.clients.starter.adapter.in.dto.GenericResponse;
import com.lulobank.clients.starter.v3.adapters.in.dto.UpdateEmailAddressRequest;
import com.lulobank.clients.starter.v3.handler.UpdateEmailAddressHandler;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1")
public class UpdateEmailAddressAdapter {

    UpdateEmailAddressHandler clientUpdateEmailAddressHandler;

    public UpdateEmailAddressAdapter(UpdateEmailAddressHandler clientUpdateEmailAddressHandler) {
        this.clientUpdateEmailAddressHandler = clientUpdateEmailAddressHandler;
    }

    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "OK"),
            @ApiResponse(
                    code = 412,
                    message = "Precondition Failed",
                    response = ErrorResponse.class),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server Error",
                    response = ErrorResponse.class)
    })
    @PutMapping(value = "clients/{idClient}/email-addresses")
    public ResponseEntity<GenericResponse> putEmailAddress(@RequestHeader final HttpHeaders headers,
                                                           @Valid @PathVariable("idClient") @NotBlank(message = "IdClient is null or empty") String idClient,
                                                           @Valid @RequestBody UpdateEmailAddressRequest updateEmailAddressRequest) {
        return clientUpdateEmailAddressHandler.updateEmailAddress(idClient, updateEmailAddressRequest);
    }
}
