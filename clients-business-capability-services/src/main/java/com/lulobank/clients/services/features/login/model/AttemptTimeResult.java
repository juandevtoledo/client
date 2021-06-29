package com.lulobank.clients.services.features.login.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AttemptTimeResult {
  private Double penalty;
  private Double timeRemaining;
  private Boolean maxAttempt;

  public AttemptTimeResult() {}

  public AttemptTimeResult(Double penalty, Double timeRemaining) {
    this.penalty = penalty;
    this.timeRemaining = timeRemaining;
  }
}
