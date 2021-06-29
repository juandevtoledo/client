package com.lulobank.clients.services.events;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionState {
  private Integer id;
  private String stateName;
}
