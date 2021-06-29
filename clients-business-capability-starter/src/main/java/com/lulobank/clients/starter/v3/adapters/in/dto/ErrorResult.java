package com.lulobank.clients.starter.v3.adapters.in.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResult {
    private String failure;
    private Integer value;
    private String detail;

    public ErrorResult(Integer value, String failure) {
        this.value = value;
        this.failure = failure;
    }

    public ErrorResult(String failure, Integer value, String detail) {
        this.failure = failure;
        this.value = value;
        this.detail = detail;
    }
}