package com.lulobank.clients.services.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckingAccount {
  private Double amount;
  private String number;
  private String country;
  private String bank;
  private String currency;
  private String city;
}
