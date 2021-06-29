package com.lulobank.clients.services.features.profile.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateEmailClientResponse {
  private String emailAddress;
  private String phonePrefix;
  private String phoneNumber;
  private String idCard;
}
