package com.lulobank.clients.services.events;

import com.lulobank.reporting.sdk.operations.dto.TypeReport;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewReportEvent {

  private String idClient;
  private TypeReport typeReport;
  private String idProduct;
  private String initialPeriod;
  private String finalPeriod;

  public NewReportEvent() {}
}
