package com.lulobank.clients.starter.v3.adapters.out.savingsaccountservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SavingAccountCreated {
    private ErrorResult error;
    private String idCbs;
    private String accountId;
}
