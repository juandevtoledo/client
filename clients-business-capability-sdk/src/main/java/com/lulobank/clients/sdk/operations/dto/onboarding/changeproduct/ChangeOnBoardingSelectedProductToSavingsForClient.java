package com.lulobank.clients.sdk.operations.dto.onboarding.changeproduct;

import com.lulobank.clients.sdk.operations.dto.AbstractCommandFeatures;
import com.lulobank.core.Command;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeOnBoardingSelectedProductToSavingsForClient extends AbstractCommandFeatures
    implements Command {
  private String idClient;

  public ChangeOnBoardingSelectedProductToSavingsForClient(String idClient) {
    this.idClient = idClient;
  }
}
