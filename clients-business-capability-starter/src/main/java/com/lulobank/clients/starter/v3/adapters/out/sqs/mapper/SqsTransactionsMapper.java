package com.lulobank.clients.starter.v3.adapters.out.sqs.mapper;

import com.lulobank.clients.starter.v3.adapters.out.sqs.dto.CheckReferralHoldsForNewClient;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SqsTransactionsMapper {
    public static CheckReferralHoldsForNewClient buildCheckReferralHoldsForNewClient(ClientsV3Entity clientsV3Entity) {
        return CheckReferralHoldsForNewClient.builder()
                .idClient(clientsV3Entity.getIdClient())
                .accountId(clientsV3Entity.getIdSavingAccount())
                .email(clientsV3Entity.getEmailAddress())
                .phonePrefix(clientsV3Entity.getPhonePrefix().toString())
                .phoneNumber(clientsV3Entity.getPhoneNumber())
                .idCbs(clientsV3Entity.getIdCbs())
                .name(clientsV3Entity.getName())
                .build();
    }
}
