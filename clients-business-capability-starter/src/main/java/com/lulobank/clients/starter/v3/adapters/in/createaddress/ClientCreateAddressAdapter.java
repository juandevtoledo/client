package com.lulobank.clients.starter.v3.adapters.in.createaddress;


import com.lulobank.clients.starter.adapter.in.dto.ErrorResponse;
import com.lulobank.clients.starter.adapter.in.dto.GenericResponse;
import com.lulobank.clients.starter.adapter.in.util.AdapterResponseUtil;
import com.lulobank.clients.starter.adapter.in.util.ValidationUtil;
import com.lulobank.clients.starter.v3.adapters.in.createaddress.handler.ClientCreateAddressHandler;
import com.lulobank.clients.starter.v3.adapters.in.dto.CreateAddressRequest;
import com.lulobank.clients.starter.v3.adapters.in.dto.CreateAddressResponse;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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
@RequestMapping("/api/v1/clients/{idClient}/main-address")
@Slf4j
@AllArgsConstructor
public class ClientCreateAddressAdapter {

    private final ClientCreateAddressHandler clientCreateAddressHandler;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "OK",
                    response = CreateAddressResponse.class),
            @ApiResponse(
                    code = 400,
                    message = "Bad Request",
                    response = ErrorResponse.class),
            @ApiResponse(
                    code = 409,
                    message = "Conflict",
                    response = ErrorResponse.class)
    })
    public ResponseEntity<GenericResponse> createAddress(@RequestHeader final HttpHeaders headers,
                                                         @Valid @PathVariable("idClient") @NotBlank(message = "IdClient is null or empty") String idClient,
                                                         @Valid @RequestBody CreateAddressRequest request,
                                                         BindingResult bindingResult) {
        return Option.of(bindingResult)
                .filter(BindingResult::hasErrors)
                .map(ValidationUtil::getResponseBindingResult)
                .map(AdapterResponseUtil::badRequest)
                .getOrElse(() -> clientCreateAddressHandler.execute(idClient, request));
    }
}
