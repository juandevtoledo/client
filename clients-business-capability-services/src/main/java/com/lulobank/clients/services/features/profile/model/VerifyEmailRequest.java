package com.lulobank.clients.services.features.profile.model;

import com.lulobank.core.Command;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VerifyEmailRequest implements Command {

  private String email;
}
