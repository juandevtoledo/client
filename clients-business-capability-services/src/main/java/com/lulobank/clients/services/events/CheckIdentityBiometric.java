package com.lulobank.clients.services.events;

import com.lulobank.core.Command;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckIdentityBiometric extends AbstractRetryFeature implements Command {
  private String idClient;
  private String idTransactionBiometric;
}
