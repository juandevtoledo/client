package com.lulobank.clients.services.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Results {
  private Float interestRate;
  private Double amount;
  private Integer installments;
  private Double maxAmountInstallment;
  private String type;
  private Integer approved;
  private String description;
  private String additionalInfo;
}
