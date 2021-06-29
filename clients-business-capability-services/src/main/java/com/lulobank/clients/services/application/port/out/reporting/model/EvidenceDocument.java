package com.lulobank.clients.services.application.port.out.reporting.model;

import lombok.Getter;

@Getter
public class EvidenceDocument {
    private static final String DEFAULT_WAY = "ASYNC_SENDING";
    private EvidenceDocumentType documentType;
    private String notificationWay;

    public EvidenceDocument(EvidenceDocumentType documentType) {
        this.documentType = documentType;
        this.notificationWay = DEFAULT_WAY;
    }

}
