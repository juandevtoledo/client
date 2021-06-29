package com.lulobank.clients.services.features.reporting.model;

import com.lulobank.clients.sdk.operations.dto.AbstractCommandFeatures;
import com.lulobank.core.Command;
import com.lulobank.reporting.sdk.operations.dto.TypeReport;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenerateClientReportStatement extends AbstractCommandFeatures implements Command {
  private String idClient;
  private TypeReport typeReport;
  private String idProduct;
  private String initialPeriod;
  private String finalPeriod;
}
