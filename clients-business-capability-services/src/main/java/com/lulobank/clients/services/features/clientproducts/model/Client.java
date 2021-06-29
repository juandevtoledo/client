package com.lulobank.clients.services.features.clientproducts.model;

import com.lulobank.clients.sdk.operations.dto.AbstractCommandFeatures;
import com.lulobank.core.Command;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Client extends AbstractCommandFeatures implements Command {

  private String idClient;
}
