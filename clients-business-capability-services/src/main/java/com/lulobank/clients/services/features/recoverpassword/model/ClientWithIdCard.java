package com.lulobank.clients.services.features.recoverpassword.model;

import com.lulobank.clients.sdk.operations.dto.AbstractCommandFeatures;
import com.lulobank.core.Command;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientWithIdCard extends AbstractCommandFeatures implements Command {
  private String idCard;

  public ClientWithIdCard() {}

  public ClientWithIdCard(String idCard) {
    this.idCard = idCard;
  }
}
