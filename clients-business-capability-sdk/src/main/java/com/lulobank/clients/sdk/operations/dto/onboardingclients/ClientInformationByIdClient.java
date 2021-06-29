package com.lulobank.clients.sdk.operations.dto.onboardingclients;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientInformationByIdClient {

  private String idClient;
  private String idCard;
  private String name;
  private String lastName;
  private String address;
  private Integer phonePrefix;
  private String phoneNumber;
  private String emailAddress;
  private String idCbs;
  private String idCbsHash;
  private OnBoardingStatus onBoardingStatus;
  private String gender;
  private String documentIssuedBy;
  private String typeDocument;
  private String expirationDate;
  private String expeditionDate;
  private LocalDate birthDate;
  private List<AccountBasicInfo> savingsAccounts;
  private String capitalizedName;
  private String initialsName;
  private AdditionalPersonalInfo additionalPersonalInfo;
  private String addressComplement;

  @Getter
  @Builder
  public static class AdditionalPersonalInfo {
      private String firstName;
      private String secondName;
      private String firstSurname;
      private String secondSurname;
  }
}
