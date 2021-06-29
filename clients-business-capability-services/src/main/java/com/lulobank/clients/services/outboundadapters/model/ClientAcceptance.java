package com.lulobank.clients.services.outboundadapters.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.lulobank.clients.services.outboundadapters.dynamoconverter.LocalDateTimeConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@DynamoDBDocument
@NoArgsConstructor
public class ClientAcceptance {
    private LocalDateTime documentAcceptancesTimestamp;
    private boolean persistedInDigitalEvidence;

    public ClientAcceptance(LocalDateTime documentAcceptancesTimestamp) {
        this.documentAcceptancesTimestamp = documentAcceptancesTimestamp;
    }

    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    public LocalDateTime getDocumentAcceptancesTimestamp() {
        return documentAcceptancesTimestamp;
    }
}