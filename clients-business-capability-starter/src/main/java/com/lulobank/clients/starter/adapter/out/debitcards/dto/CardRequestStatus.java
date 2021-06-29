package com.lulobank.clients.starter.adapter.out.debitcards.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CardRequestStatus {
    private String status;
    private String detail;
    private String statusDate;
}
