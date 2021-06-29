package com.lulobank.clients.services.events;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreditAccepted extends CheckPointClient {
  private String idClient;
}
