package com.lulobank.clients.services.application.port.out.savingsaccounts.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SavingAccount {
    private String idSavingAccount;
    private String state;
    private String type;
    private Boolean simpleDeposit;
    private Boolean gmf;
    private Balance balance;
    private String creationDate;
}
