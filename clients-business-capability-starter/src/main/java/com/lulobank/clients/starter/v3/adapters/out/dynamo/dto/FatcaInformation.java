package com.lulobank.clients.starter.v3.adapters.out.dynamo.dto;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.lulobank.clients.services.outboundadapters.dynamoconverter.LocalDateTimeConverter;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@DynamoDBDocument
public class FatcaInformation {
    private boolean fatcaResponsibility;
    private String countryCode;
    private String countryName;
    private String tin;
    private String tinObservation;
    private LocalDateTime declaredDate;
    private String status;

    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    @DynamoDBAttribute(attributeName = "declaredDate")
    public LocalDateTime getDeclaredDate() {
        return declaredDate;
    }

    public void setDeclaredDate(LocalDateTime declaredDate) {
        this.declaredDate = declaredDate;
    }
}
