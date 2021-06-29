package com.lulobank.clients.starter.v3.adapters.in.phone;

import com.lulobank.clients.starter.adapter.in.dto.GenericResponse;
import com.lulobank.clients.starter.v3.adapters.in.phone.dto.UpdatePhoneRequest;
import com.lulobank.clients.starter.v3.handler.phone.UpdatePhoneNumberHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/internal/client/{idClient}/phone-numbers")
@RequiredArgsConstructor
public class UpdatePhoneAdapter {

    private final UpdatePhoneNumberHandler updatePhoneNumberHandler;

    @PostMapping
    public ResponseEntity<GenericResponse> updatePhoneNumber(@RequestHeader final HttpHeaders headers,
                                                                @PathVariable("idClient") final String idClient,
                                                                @Valid @RequestBody final UpdatePhoneRequest request) {
        return updatePhoneNumberHandler.updatePhone(idClient, request);
    }
}
