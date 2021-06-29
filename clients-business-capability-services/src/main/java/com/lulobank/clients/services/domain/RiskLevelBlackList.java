package com.lulobank.clients.services.domain;

import lombok.Getter;

@Getter
public enum RiskLevelBlackList {
  NO_RISK("1"),
  MID_RISK("2"),
  HIGH_RISK("3")
  ;

  RiskLevelBlackList(String level) {
    this.level = level;
  }

  private String level;

}
