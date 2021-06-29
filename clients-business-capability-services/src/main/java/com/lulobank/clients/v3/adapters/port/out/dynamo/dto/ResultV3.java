package com.lulobank.clients.v3.adapters.port.out.dynamo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResultV3 {
    private Float interestRate;
    private Double amount;
    private Integer installments;
    private Double maxAmountInstallment;
    private String type;
}
