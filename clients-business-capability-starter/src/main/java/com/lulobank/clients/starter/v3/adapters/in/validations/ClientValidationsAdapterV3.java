package com.lulobank.clients.starter.v3.adapters.in.validations;

import com.lulobank.clients.starter.adapter.in.dto.GenericResponse;
import com.lulobank.clients.starter.v3.handler.ClientValidationsHandler;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@RequestMapping("validations")
@CrossOrigin(origins = "*")
public class ClientValidationsAdapterV3 {

    private final ClientValidationsHandler clientValidationsHandler;

    public ClientValidationsAdapterV3(ClientValidationsHandler clientValidationsHandler){
        this.clientValidationsHandler = clientValidationsHandler;
    }

    @GetMapping(value = "/email/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenericResponse> validateEmail(@RequestHeader final HttpHeaders headers,
                                                         @PathVariable("email") final String email){

        return clientValidationsHandler.validateEmail(email.toLowerCase(Locale.ENGLISH),headers.toSingleValueMap());

    }
}
