package com.lulobank.clients.starter.v3.adapters.out.sqs.mapper;

import com.lulobank.clients.starter.v3.adapters.out.sqs.dto.SendSignedDocumentGroupEvent;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;

public class SqsReportingMapper {

    private SqsReportingMapper() {
    }

    public static SendSignedDocumentGroupEvent buildSignedDocumentEventBlacklisted(ClientsV3Entity clientEntity) {
        return SendSignedDocumentGroupEvent.builder()
                .idClient(clientEntity.getIdClient())
                .emailAddress(clientEntity.getEmailAddress())
                .idSignedDocumentGroup("NEW_CLIENT_DOCUMENTS")
                .build();
    }

    public static SendSignedDocumentGroupEvent buildSignedDocumentCatDocument(ClientsV3Entity clientEntity) {
        return SendSignedDocumentGroupEvent.builder()
                .idClient(clientEntity.getIdClient())
                .emailAddress(clientEntity.getEmailAddress())
                .idSignedDocumentGroup("CATS_DOCUMENTS")
                .build();
    }
}
