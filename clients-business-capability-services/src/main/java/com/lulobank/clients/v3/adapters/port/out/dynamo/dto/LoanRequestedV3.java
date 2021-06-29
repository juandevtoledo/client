package com.lulobank.clients.v3.adapters.port.out.dynamo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoanRequestedV3 {
    private String status;
    private LoanClientRequestedV3 loanClientRequested;

    public LoanRequestedV3(String status) {
        this.status = status;
    }
}
