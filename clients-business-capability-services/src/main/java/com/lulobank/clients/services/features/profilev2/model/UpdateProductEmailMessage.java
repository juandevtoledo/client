package com.lulobank.clients.services.features.profilev2.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UpdateProductEmailMessage {

  private String idClient;
  private String newEmail;

}
