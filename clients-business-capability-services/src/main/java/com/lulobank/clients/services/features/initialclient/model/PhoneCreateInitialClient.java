package com.lulobank.clients.services.features.initialclient.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhoneCreateInitialClient {

  private static final long serialVersionUID = 1L;
  private static final int DEFAULT_PHONE_PREFIX_COLOMBIA = 57;
  private int prefix = DEFAULT_PHONE_PREFIX_COLOMBIA;
  private String number;
  private Boolean verified;
}
