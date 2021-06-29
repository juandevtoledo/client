package com.lulobank.clients.starter.adapter.out.savingsaccounts.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SavingAccountType {
    private String idSavingAccount;
    private String state;
    private String type;
    private Boolean simpleDeposit;
    private Boolean gmf;
    private SavingAccountBalance balance;
    private String creationDate;
}