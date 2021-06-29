package com.lulobank.clients.services.features.onboardingclients.model;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhoneCreateClientRequest implements Serializable {
  private static final long serialVersionUID = 1L;
  private static final int DEFAULT_PHONE_PREFIX_COLOMBIA = 57;
  private int prefix = DEFAULT_PHONE_PREFIX_COLOMBIA;
  private String number;
  private Boolean verified;
  private PhoneDeviceCreateClientRequest deviceInfo;
}
