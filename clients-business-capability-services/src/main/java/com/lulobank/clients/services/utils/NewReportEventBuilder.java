package com.lulobank.clients.services.utils;

import com.lulobank.clients.services.events.NewReportEvent;
import com.lulobank.reporting.sdk.operations.dto.TypeReport;

public class NewReportEventBuilder {

  private String idClient;
  private TypeReport typeReport;
  private String idProduct;
  private String initialPeriod;
  private String finalPeriod;

  private NewReportEventBuilder() {}

  public static NewReportEventBuilder newReportEventBuilder() {
    return new NewReportEventBuilder();
  }

  public NewReportEventBuilder withIdClient(String idClient) {
    this.idClient = idClient;
    return this;
  }

  public NewReportEventBuilder withTypeReport(TypeReport typeReport) {
    this.typeReport = typeReport;
    return this;
  }

  public NewReportEventBuilder withIdProduct(String idProduct) {
    this.idProduct = idProduct;
    return this;
  }

  public NewReportEventBuilder withInitialPeriod(String initialPeriod) {
    this.initialPeriod = initialPeriod;
    return this;
  }

  public NewReportEventBuilder withFinalPeriod(String finalPeriod) {
    this.finalPeriod = finalPeriod;
    return this;
  }

  public NewReportEvent build() {
    NewReportEvent newReportEvent = new NewReportEvent();
    newReportEvent.setIdClient(this.idClient);
    newReportEvent.setIdProduct(this.idProduct);
    newReportEvent.setTypeReport(this.typeReport);
    newReportEvent.setInitialPeriod(this.initialPeriod);
    newReportEvent.setFinalPeriod(this.finalPeriod);
    return newReportEvent;
  }
}
