package com.lulobank.clients.services.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static com.lulobank.clients.services.utils.StringUtils.getStringWithOutSpaces;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdditionalPersonalInformation {
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

  public void setFirstName(String firstName) {
    this.firstName = getStringWithOutSpaces(firstName);
  }

  public void setSecondName(String secondName) {
    this.secondName = getStringWithOutSpaces(secondName);
  }

  public void setFirstSurname(String firstSurname) {
    this.firstSurname = getStringWithOutSpaces(firstSurname);
  }

  public void setSecondSurname(String secondSurname) {
    this.secondSurname = getStringWithOutSpaces(secondSurname);
  }

}
