package com.lulobank.clients.sdk.operations.dto;

import com.lulobank.core.Command;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VerifyEmailResponse implements Command {
  private String email;
  private boolean isVerified;
}
