package com.lulobank.clients.services.utils;

import lombok.Getter;
import lombok.experimental.Accessors;

public enum ClientsErrorResponse {
  UNPROCESSABLE_REQUEST("GEN_001"),
  TIMESTAMP_ERROR("GEN_101"),
  CLIENT_NOT_FOUND_ERROR("GEN_101"),
  IDENTITY_PROVIDER_ERROR("GEN_201"),
  DIGITAL_EVIDENCE_ERROR("REP_101"),
  JWT_GENERATION_ERROR("AUT_201"),
  CLIENT_NOTIFICATION_ERROR("CAL_201"),
  CUSTOMER_SERVICE_ERROR("CUS_101"),
  ;

  @Getter
  @Accessors(fluent = true)
  private final String code;

  ClientsErrorResponse(String code) {
    this.code = code;
  }

}
