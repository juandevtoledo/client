package com.lulobank.clients.services.features.onboardingclients.model;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailCreateClientRequest implements Serializable {

  private static final long serialVersionUID = 1L;

  private String address;
  private Boolean verified;
}
