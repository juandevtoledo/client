package com.lulobank.clients.services.domain;

import java.util.Arrays;

public enum DocumentType {
  NOT_FOUND("0", "INVALIDO"),
  CC("1", "CC");
  private final String idType;
  private final String name;

  DocumentType(String idType, String name) {
    this.idType = idType;
    this.name = name;
  }

  public static String getName(String idType) {
    return Arrays.stream(values())
        .filter(dt -> dt.idType.equals(idType))
        .findFirst()
        .orElse(NOT_FOUND)
        .name;
  }

  public static String getType(String name) {
    return Arrays.stream(values())
        .filter(dt -> dt.name.equals(name))
        .findFirst()
        .orElse(NOT_FOUND)
        .idType;
  }
}
