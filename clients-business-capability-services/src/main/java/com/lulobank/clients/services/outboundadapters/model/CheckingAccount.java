package com.lulobank.clients.services.outboundadapters.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

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
