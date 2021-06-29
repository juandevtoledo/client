package com.lulobank.clients.services.events;

import com.lulobank.clients.sdk.operations.dto.economicinformation.EmployeeCompany;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EconomicInformationEvent {
  private String occupationType;
  private EmployeeCompany employeeCompany;
  private String economicActivity;
  private BigDecimal monthlyIncome;
  private BigDecimal monthlyOutcome;
  private BigDecimal additionalIncome;
  private BigDecimal assets;
  private BigDecimal liabilities;
  private String savingPurpose;
  private String typeSaving;
}
