package com.lulobank.clients.services.features.profile.model;

import com.lulobank.clients.sdk.operations.dto.AbstractCommandFeatures;
import com.lulobank.core.Command;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateEmailClientRequest extends AbstractCommandFeatures implements Command {
  private String idClient;
  private String oldEmail;
  private String newEmail;
  private String password;
}
