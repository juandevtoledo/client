package com.lulobank.clients.starter.v3.adapters.out.dynamo.dto;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@DynamoDBDocument
public class EconomicInformation {
  private String occupationType;
  private Company company;
  private String economicActivity;
  private BigDecimal monthlyIncome;
  private BigDecimal monthlyOutcome;
  private BigDecimal additionalIncome;
  private BigDecimal assets;
  private BigDecimal liabilities;
  private String savingPurpose;
  private String typeSaving;
}
