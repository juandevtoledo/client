package com.lulobank.clients.services.features.riskengine.model;

import com.lulobank.clients.services.features.onboardingclients.model.PhoneCreateClientRequest;
import com.lulobank.core.Command;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientWithIdCardInformation implements Command {
  private String idCard;
  private String dateOfIssue;
  private PhoneCreateClientRequest phone;
}
