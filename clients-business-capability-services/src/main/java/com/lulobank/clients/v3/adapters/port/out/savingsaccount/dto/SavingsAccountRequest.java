package com.lulobank.clients.v3.adapters.port.out.savingsaccount.dto;

import com.lulobank.clients.v3.dto.ClientInformationV3;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SavingsAccountRequest {

    private String idClient;
    private ClientInformationV3 clientInformation;
    private boolean simpleDeposit;

}
