package com.lulobank.clients.services.outboundadapters.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

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
  private String economicActivityRiskLevel;
  private String economicActivityRiskLevelMonitor;
}
