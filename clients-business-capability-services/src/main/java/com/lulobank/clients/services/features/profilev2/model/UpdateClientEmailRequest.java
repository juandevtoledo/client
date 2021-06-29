package com.lulobank.clients.services.features.profilev2.model;

import com.lulobank.clients.sdk.operations.dto.AbstractCommandFeatures;
import lombok.Getter;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class UpdateClientEmailRequest extends AbstractCommandFeatures {

  @JsonIgnore
  private String idClient;

  @NotBlank(message = "new email is empty or null")
  @Email(message = "new email is invalid")
  private String newEmail;

}
