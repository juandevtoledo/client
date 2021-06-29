package com.lulobank.clients.services.features.changepassword.model;

import com.lulobank.core.Command;
import com.lulobank.core.utils.IgnoreValidation;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Password implements Command {
  private String idClient;
  private String emailAddress;
  private String password;
  @IgnoreValidation private String phonePrefix;
  @IgnoreValidation private String phoneNumber;

  public Password(String idClient) {
    this.idClient = idClient;
  }

  public Password() {}
}
