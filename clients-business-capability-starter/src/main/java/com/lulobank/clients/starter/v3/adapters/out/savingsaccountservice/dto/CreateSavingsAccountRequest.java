package com.lulobank.clients.starter.v3.adapters.out.savingsaccountservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateSavingsAccountRequest {
    private ClientInformation clientInformation;
    private boolean simpleDeposit;
}
