package com.lulobank.clients.services.domain;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Phone extends ValueObject<Phone> {

  private int prefix;
  private String number;
  private Boolean verified;
  private Device deviceInfo;

  public Phone() {}

  public Phone(int prefix, String number) {
    this.prefix = prefix;
    this.number = number;
    this.verified = false;
  }

  public Phone(int prefix, String number, Boolean verified, Device device) {
    this.prefix = prefix;
    this.number = number;
    this.verified = verified;
    this.deviceInfo = device;
  }

  public void wasValidated() {
    verified = true;
  }

  @Override
  protected List<Supplier> supplyGettersToIncludeInEqualityCheck() {
    return Arrays.asList(this::getPrefix, this::getNumber);
  }
}
