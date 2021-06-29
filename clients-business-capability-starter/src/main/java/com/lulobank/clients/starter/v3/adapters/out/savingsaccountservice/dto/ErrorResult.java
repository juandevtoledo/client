package com.lulobank.clients.starter.v3.adapters.out.savingsaccountservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResult {
    private String failure;
    private Integer value;
    private String detail;
}
