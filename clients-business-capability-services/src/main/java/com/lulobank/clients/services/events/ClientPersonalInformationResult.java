package com.lulobank.clients.services.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientPersonalInformationResult {
  private String name;
  private String lastName;
  private String birthDate;
  private IdDocument idDocument;
  private String gender;
  private AdditionalPersonalInformation additionalPersonalInformation;
}
