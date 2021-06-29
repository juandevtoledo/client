package com.lulobank.clients.services.events;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CBSCreated {
  private String idClient;
  private String idCbs;
  private String idCbsHash;
}
