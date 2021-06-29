package com.lulobank.clients.services.features.onboardingclients.model;

import com.lulobank.core.Command;
import lombok.Getter;
import lombok.Setter;

/**
 * @deprecated (see ClientsDemographicAdapterV3
 * new url /api/v3/client/{idClient}/demographic) 
 */

@Getter
@Setter
@Deprecated
public class GetDemographicInfoByIdClient implements Command {

  private String idClient;

  public GetDemographicInfoByIdClient(String idClient) {
    this.idClient = idClient;
  }
}
