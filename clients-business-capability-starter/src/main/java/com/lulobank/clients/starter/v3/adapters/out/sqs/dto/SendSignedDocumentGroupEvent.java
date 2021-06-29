package com.lulobank.clients.starter.v3.adapters.out.sqs.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SendSignedDocumentGroupEvent {
    private String emailAddress;
    private String idClient;
    private String idSignedDocumentGroup;
}
