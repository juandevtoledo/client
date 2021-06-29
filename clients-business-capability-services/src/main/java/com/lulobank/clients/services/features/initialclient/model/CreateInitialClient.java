package com.lulobank.clients.services.features.initialclient.model;

import com.lulobank.clients.sdk.operations.dto.AbstractCommandFeatures;
import com.lulobank.clients.services.features.onboardingclients.model.EmailCreateClientRequest;
import com.lulobank.clients.services.utils.ProductTypeEnum;
import com.lulobank.core.Command;
import com.lulobank.core.utils.IgnoreValidation;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateInitialClient extends AbstractCommandFeatures implements Command {

  private EmailCreateClientRequest emailCreateClientRequest;
  private PhoneCreateInitialClient phoneCreateInitialClient;
  private String password;
  @IgnoreValidation private String documentAcceptancesTimestamp;
  private ProductTypeEnum selectedProduct;

}
