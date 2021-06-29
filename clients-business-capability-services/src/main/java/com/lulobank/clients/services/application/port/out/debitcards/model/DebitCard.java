package com.lulobank.clients.services.application.port.out.debitcards.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DebitCard {
    private String expirationDate;
    private String holderName;
    private String cardNumberMask;
    private String color;
    private String fullName;
}
