package com.lulobank.clients.starter.adapter.in.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse extends GenericResponse {
    private String failure;
    private String code;
    private String detail;

    public ErrorResponse(String failure, String code) {
        this.failure = failure;
        this.code = code;
    }
}

