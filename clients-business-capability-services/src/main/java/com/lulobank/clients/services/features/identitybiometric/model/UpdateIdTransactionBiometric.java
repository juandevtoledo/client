package com.lulobank.clients.services.features.identitybiometric.model;

import com.lulobank.core.Command;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateIdTransactionBiometric implements Command {
  private String idClient;
  private String idTransactionBiometric;
}
