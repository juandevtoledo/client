package com.lulobank.clients.services.features.recoverpassword.model;

import com.lulobank.core.Command;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RecoverPassword implements Command {
  private int prefix;
  private String phoneNumber;

  public RecoverPassword() {}

  public RecoverPassword(int prefix, String phoneNumber) {
    this.prefix = prefix;
    this.phoneNumber = phoneNumber;
  }
}
