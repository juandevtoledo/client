package com.lulobank.clients.starter.v3.adapters.out.dynamo.dto;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.Getter;
import lombok.Setter;

import static com.lulobank.clients.services.utils.StringUtils.getStringWithOutSpaces;

@Getter
@Setter
@DynamoDBDocument
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
