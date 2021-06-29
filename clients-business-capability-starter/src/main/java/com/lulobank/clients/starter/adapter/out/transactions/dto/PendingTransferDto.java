package com.lulobank.clients.starter.adapter.out.transactions.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PendingTransferDto {
    private String phoneNumber;
    private String originName;
    private Double amount;
    private String currency;
    private LocalDateTime dueOn;
    private String transferId;
    private String transferType;
    private String channel;
}
