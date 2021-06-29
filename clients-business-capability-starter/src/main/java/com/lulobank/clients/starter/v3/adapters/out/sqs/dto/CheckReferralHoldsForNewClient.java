package com.lulobank.clients.starter.v3.adapters.out.sqs.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CheckReferralHoldsForNewClient {
    private String idClient;
    private String email;
    private String phonePrefix;
    private String phoneNumber;
    private String accountId;
    private String idCbs;
    private String name;
}
