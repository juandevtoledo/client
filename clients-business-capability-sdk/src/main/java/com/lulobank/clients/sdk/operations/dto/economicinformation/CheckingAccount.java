package com.lulobank.clients.sdk.operations.dto.economicinformation;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CheckingAccount implements Serializable {
  private static final long serialVersionUID = 1L;
  private BigDecimal amount;
  private String bank;
  private String number;
  private String currency;
  private String country;
  private String city;

  public CheckingAccount(
      BigDecimal amount, String number, String country, String bank, String currency, String city) {
    this.amount = amount;
    this.currency = currency;
    this.bank = bank;
    this.city = city;
    this.country = country;
    this.number = number;
  }
}
