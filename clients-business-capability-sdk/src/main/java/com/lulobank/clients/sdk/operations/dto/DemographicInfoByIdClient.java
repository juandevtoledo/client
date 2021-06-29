package com.lulobank.clients.sdk.operations.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @deprecated (see ClientsDemographicAdapterV3
 * new url /api/v3/client/{idClient}/demographic) 
 */

@Getter
@Setter
@Deprecated
public class DemographicInfoByIdClient {

  private String idClient;
  private String name;
  private String lastName;
  private String address;
  private String addressPrefix;
  private String code;
  private String addressComplement;
  private String department;
  private String departmentId;
  private String city;
  private String cityId;
}
