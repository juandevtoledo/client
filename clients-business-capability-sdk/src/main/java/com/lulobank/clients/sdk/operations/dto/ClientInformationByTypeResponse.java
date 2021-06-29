package com.lulobank.clients.sdk.operations.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientInformationByTypeResponse {
  private String idClient;
  private String idCard;
  private String name;
  private String lastName;
  private Integer phonePrefix;
  private String phoneNumber;
  private String emailAddress;
  private Boolean emailVerified;
  private String idCbs;
  private String idCbsHash;
  private String biometricStatus;
  private String checkpoint;
  private String productSelected;
  private String idKeycloak;
  private boolean digitalStorageStatus;
  private String documentAcceptancesTimestamp;
  private boolean persistedInDigitalEvidence;
  private boolean customerCreatedStatus;
  private boolean pep;
  private String riskLevel;
  private String blacklistState;
  private String whitelistExpirationDate;
}