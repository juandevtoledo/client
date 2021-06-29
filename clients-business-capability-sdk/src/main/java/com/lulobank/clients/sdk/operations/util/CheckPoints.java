package com.lulobank.clients.sdk.operations.util;

public enum CheckPoints {
  NONE(0),
  ON_BOARDING(1),
  CLIENT_VERIFICATION(2),
  BLACKLIST_STARTED(3),
  BLACKLISTED(4),
  BLACKLIST_FINISHED(5),
  PEP_FINISHED(6),
  CLIENT_ADDRESS_FINISHED(7),
  ECONOMIC_INFO_FINISHED(8),
  FATCA_FINISHED(9),
  SAVING_ACCOUNT_CREATED(10),
  LOAN_CREATED(11),
  FINISH_ON_BOARDING(12),
  ;

  private Integer order;

  CheckPoints(Integer order) {
    this.order = order;
  }

  public Integer getOrder() {
    return order;
  }

}
