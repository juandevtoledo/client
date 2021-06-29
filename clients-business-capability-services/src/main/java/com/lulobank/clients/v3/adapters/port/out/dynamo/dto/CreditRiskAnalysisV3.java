package com.lulobank.clients.v3.adapters.port.out.dynamo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreditRiskAnalysisV3 {
  private String status;
  private List<ResultV3> results;

  public CreditRiskAnalysisV3() {}
}
