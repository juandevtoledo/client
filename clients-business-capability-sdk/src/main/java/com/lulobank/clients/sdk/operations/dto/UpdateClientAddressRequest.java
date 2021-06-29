package com.lulobank.clients.sdk.operations.dto;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lulobank.core.Command;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateClientAddressRequest extends AbstractCommandFeatures implements Command {

  private String idClient;
  
  private String checkpoint;

  @NotBlank(message = "address is null or empty")
  private String address;

  @NotBlank(message = "addressPrefix is null or empty")
  private String addressPrefix;

  private String addressComplement;

  @NotBlank(message = "city is null or empty")
  private String city;

  @NotBlank(message = "cityId is null or empty")
  private String cityId;

  @NotBlank(message = "department is null or empty")
  private String department;

  @NotBlank(message = "departmentId is null or empty")
  private String departmentId;

  @JsonIgnore
  private boolean sendNotification;

  private String code;
}
