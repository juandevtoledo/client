package com.lulobank.clients.starter.v3.adapters.out.dynamo.dto;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@DynamoDBDocument
public class CheckingAccount {
  private BigDecimal amount;
  private String number;
  private String country;
  private String bank;
  private String currency;
  private String city;
}
