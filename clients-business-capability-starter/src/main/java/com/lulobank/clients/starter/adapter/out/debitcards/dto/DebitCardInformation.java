package com.lulobank.clients.starter.adapter.out.debitcards.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DebitCardInformation {
    private String expirationDate;
    private String holderName;
    private String cardNumberMask;
    private String color;
    private String fullName;
}
