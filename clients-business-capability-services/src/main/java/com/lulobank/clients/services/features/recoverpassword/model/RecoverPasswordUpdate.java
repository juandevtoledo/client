package com.lulobank.clients.services.features.recoverpassword.model;

import com.lulobank.clients.sdk.operations.dto.AbstractCommandFeatures;
import com.lulobank.core.Command;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecoverPasswordUpdate extends AbstractCommandFeatures implements Command {
  private String emailAddress;
  private String verificationCode;
  private String newPassword;
  private String idCard;
}
