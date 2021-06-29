package com.lulobank.clients.services.features;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RetriesOption {
  private Integer maxRetries;
  private Map<Integer, Integer> delayOptions;
}
