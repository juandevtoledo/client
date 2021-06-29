package com.lulobank.clients.v3.adapters.port.out.dynamo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdditionalPersonalInfoV3 {
  private String firstName;
  private String secondName;
  private String firstSurname;
  private String secondSurname;
  private String street;
  private String spouse;
  private String dateOfIdentification;
  private String dateOfDeath;
  private String marriageDate;
  private String instruction;
  private String home;
  private String maritalStatus;
  private String placeBirth;
  private String nationality;
  private String profession;
}
