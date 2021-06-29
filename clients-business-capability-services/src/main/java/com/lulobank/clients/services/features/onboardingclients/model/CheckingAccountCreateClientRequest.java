package com.lulobank.clients.services.features.onboardingclients.model;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckingAccountCreateClientRequest implements Serializable {
  private static final long serialVersionUID = 1L;
  private Double amount;
  private String bank;
  private String number;
  private String currency;
  private String country;
  private String city;

  public CheckingAccountCreateClientRequest() {
    // Empty constructor required for mapping in Jackson
  }

  public CheckingAccountCreateClientRequest(
      double amount, String number, String country, String bank, String currency, String city) {
    this.amount = amount;
    this.currency = currency;
    this.bank = bank;
    this.city = city;
    this.country = country;
    this.number = number;
  }
}
