package com.lulobank.clients.starter.adapter.out.debitcards.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DebitCardStatus {
    private CurrentCardStatus currentCard;
    private CardRequestStatus cardRequest;
}
