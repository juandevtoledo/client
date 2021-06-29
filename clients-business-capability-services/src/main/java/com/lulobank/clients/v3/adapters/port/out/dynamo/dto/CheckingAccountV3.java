package com.lulobank.clients.v3.adapters.port.out.dynamo.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CheckingAccountV3 {
  private BigDecimal amount;
  private String number;
  private String country;
  private String bank;
  private String currency;
  private String city;
}
