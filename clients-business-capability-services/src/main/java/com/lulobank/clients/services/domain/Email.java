package com.lulobank.clients.services.domain;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Email extends ValueObject<Email> {

  private static final long serialVersionUID = 1L;

  private String address;
  private Boolean verified;

  public Email() {}

  public Email(String address) {
    this.address = address;
    this.verified = false;
  }

  public Email(String address, Boolean verified) {
    this.address = address;
    this.verified = verified;
  }

  public void wasValidated() {
    verified = true;
  }

  @Override
  protected List<Supplier> supplyGettersToIncludeInEqualityCheck() {
    return Arrays.asList(this::getAddress);
  }

  @Override
  public boolean equals(Object obj) {
    // If the object is compared with itself then return true
    if (obj == this) {
      return true;
    }
    /* Check if o is an instance of Complex or not
    "null instanceof [type]" also returns false */
    if (!(obj instanceof Email)) {
      return false;
    }
    Email mail = (Email) obj;
    return (this.address.equals(mail.address) && this.verified.equals(mail.verified));
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + address.hashCode();
    result = 31 * result + verified.hashCode();
    return result;
  }
}
