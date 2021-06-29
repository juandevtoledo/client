package com.lulobank.clients.services.features.profilev2.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateEmailNotification {
  private String emailAddress;
  private String phonePrefix;
  private String phoneNumber;
  private String idCard;
}
