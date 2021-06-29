package com.lulobank.clients.sdk.operations.dto.economicinformation;

import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeCompany {
  @Size(max = 100)
  private String name;
  @Size(max = 100)
  private String state;
  @Size(max = 100)
  private String city;
}
