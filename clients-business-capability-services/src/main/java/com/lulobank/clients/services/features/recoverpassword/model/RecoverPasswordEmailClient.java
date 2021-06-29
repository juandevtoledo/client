package com.lulobank.clients.services.features.recoverpassword.model;

import com.lulobank.core.Command;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecoverPasswordEmailClient implements Command {
  private String emailAddress;

  public RecoverPasswordEmailClient(String emailAddress) {
    this.emailAddress = emailAddress;
  }
}
