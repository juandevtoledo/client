package com.lulobank.clients.services.features.onboardingclients.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientInformationByPhone {

  private String idClient;
  private String name;
  private String lastName;
  private Integer phonePrefix;
  private String phoneNumber;
  private String emailAddress;
  private String idCbs;
  private String idCbsHash;
}
