package com.lulobank.clients.v3.adapters.port.out.dynamo.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class EconomicInformationV3 {
  private String occupationType;
  private CompanyV3 company;
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
