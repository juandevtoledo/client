package com.lulobank.clients.services.features.login.model;

import com.lulobank.core.Command;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUp implements Serializable, Command {

  private static final long serialVersionUID = 1L;

  private String username;
  private String password;

  public SignUp() {}
}
