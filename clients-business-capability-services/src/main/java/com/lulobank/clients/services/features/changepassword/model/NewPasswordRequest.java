package com.lulobank.clients.services.features.changepassword.model;

import com.lulobank.clients.sdk.operations.dto.AbstractCommandFeatures;
import com.lulobank.core.Command;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewPasswordRequest extends AbstractCommandFeatures implements Command {

  private String idClient;
  private String emailAddress;
  private String newPassword;
  private String oldPassword;
  private String accessToken;
}
