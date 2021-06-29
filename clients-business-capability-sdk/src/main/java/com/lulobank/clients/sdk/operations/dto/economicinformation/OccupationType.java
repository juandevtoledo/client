package com.lulobank.clients.sdk.operations.dto.economicinformation;

public enum OccupationType {
  EMPLOYEE("0010"),
  RETIRED("0050"),
  SELF_EMPLOYEE("");

  private final String code;

  OccupationType(String code){
    this.code= code;
  }


  public String getCode() {
    return code;
  }
}
