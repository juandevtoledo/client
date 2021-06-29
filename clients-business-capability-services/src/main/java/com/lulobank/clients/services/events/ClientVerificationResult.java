package com.lulobank.clients.services.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lulobank.core.Command;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientVerificationResult extends AbstractRetryFeature implements Command {
  private String idTransactionBiometric;
  private String status;
  private ClientPersonalInformationResult clientPersonalInformation;
  private TransactionState transactionState;
  private BlacklistResult blacklist;
}
