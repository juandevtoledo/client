package com.lulobank.clients.services.events;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractRetryFeature {
  private int delayInSeconds;
  private int retryCount;
}
