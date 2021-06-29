package com.lulobank.clients.v3.adapters.port.out.dynamo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OnBoardingStatusV3 {
    private String checkpoint;
    private String productSelected;
    private LoanClientRequestedV3 loanClientRequested;

    public OnBoardingStatusV3(String checkpoint, String productSelected) {
        this.checkpoint = checkpoint;
        this.productSelected = productSelected;
    }
}
