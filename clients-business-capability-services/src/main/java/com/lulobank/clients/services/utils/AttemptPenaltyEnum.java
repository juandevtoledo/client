package com.lulobank.clients.services.utils;

public enum AttemptPenaltyEnum {
  ATTEMPT(0D, 3),
  FIRST(2d, 6),
  SECOND(10d, 7),
  THIRD(20d, 8),
  FOURTH(30d, 9),
  FINAL(99d, 99);

  private Double minutes;
  private Integer failsAttempts;

  AttemptPenaltyEnum(Double minutes, Integer failsAttempts) {
    this.minutes = minutes;
    this.failsAttempts = failsAttempts;
  }

  public Double getMinutes() {
    return minutes;
  }

  public Integer getFailsAttempts() {
    return failsAttempts;
  }
}
