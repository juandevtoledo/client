package com.lulobank.clients.services.events;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IdentityVerificationAccepted extends CheckPointClient {
  private String idApplicant;
}
