package com.lulobank.clients.v3.usecase.productoffers.command;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateClientProductOfferRequest {
    private final String idClient;
    private final String type;
    private final int value;
}
