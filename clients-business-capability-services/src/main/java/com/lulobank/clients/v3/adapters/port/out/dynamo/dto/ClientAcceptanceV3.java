package com.lulobank.clients.v3.adapters.port.out.dynamo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ClientAcceptanceV3 {
    private LocalDateTime documentAcceptancesTimestamp;
    private boolean persistedInDigitalEvidence;

    public ClientAcceptanceV3(LocalDateTime documentAcceptancesTimestamp) {
        this.documentAcceptancesTimestamp = documentAcceptancesTimestamp;
    }

    public LocalDateTime getDocumentAcceptancesTimestamp() {
        return documentAcceptancesTimestamp;
    }
}