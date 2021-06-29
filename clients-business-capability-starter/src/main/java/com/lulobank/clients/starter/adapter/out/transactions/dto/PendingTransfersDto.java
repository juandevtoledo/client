package com.lulobank.clients.starter.adapter.out.transactions.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PendingTransfersDto {
    private List<PendingTransferDto> content;
}
