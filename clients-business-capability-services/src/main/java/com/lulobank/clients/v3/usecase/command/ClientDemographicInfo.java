package com.lulobank.clients.v3.usecase.command;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClientDemographicInfo {
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
