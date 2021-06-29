package com.lulobank.clients.sdk.operations.dto.resetidentitybiometric;

import com.lulobank.core.Command;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientToReset implements Command {
  private String idClient;
}
