package com.lulobank.clients.v3.adapters.port.out.credits.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetActiveLoandResponse {
    private final String idCredit;
    private final String idLoanCBS;
    private final String state;
    private final Integer installments;
    private final LocalDateTime createOn;
}
