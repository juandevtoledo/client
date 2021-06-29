package com.lulobank.clients.sdk.operations.dto;

import com.lulobank.core.Command;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientLoanRequested implements Command {
  private String idClient;
  private Double amount;
  private String loanPurpose;
}
