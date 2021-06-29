package com.lulobank.clients.v3.usecase.productoffers.command;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetClientProductOfferRequest {
    private final String idClient;
    private final Map<String,String> auth;
}
