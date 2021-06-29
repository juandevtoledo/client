package com.lulobank.clients.services.ports.out.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RiskOfferResponse {
    private final Double amount;
    private final Float interestRate;
    private final Integer installments;
}
