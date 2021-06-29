package com.lulobank.clients.starter.adapter.in.util;

import com.lulobank.clients.starter.adapter.in.dto.ErrorResponse;
import com.lulobank.clients.starter.adapter.in.dto.GenericResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

public class AdapterResponseUtil {

    public static ResponseEntity<GenericResponse> ok(GenericResponse response) {
        return new ResponseEntity<>(response, OK);
    }

    public static ResponseEntity<GenericResponse> accepted(GenericResponse response) {
        return new ResponseEntity<>(response, ACCEPTED);
    }

    public static ResponseEntity<GenericResponse> ok() {
        return new ResponseEntity<>(OK);
    }

    public static ResponseEntity<GenericResponse> badRequest(ErrorResponse errorResponse) {
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<GenericResponse> error(ErrorResponse errorResponse, HttpStatus httpStatus) {
        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    public static ResponseEntity<GenericResponse> created(GenericResponse response) {
        return new ResponseEntity<>(response, CREATED);
    }

}
