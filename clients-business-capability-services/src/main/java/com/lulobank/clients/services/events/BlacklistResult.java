package com.lulobank.clients.services.events;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlacklistResult {

  private String status;
  private String reportDate;
  private String resultRiskLevel;
}
