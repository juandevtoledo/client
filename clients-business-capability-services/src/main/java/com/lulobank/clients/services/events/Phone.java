package com.lulobank.clients.services.events;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Phone {

  private String number;
  private String prefix;

  public Phone(String number, String prefix) {
    this.number = number;
    this.prefix = prefix;
  }

}
