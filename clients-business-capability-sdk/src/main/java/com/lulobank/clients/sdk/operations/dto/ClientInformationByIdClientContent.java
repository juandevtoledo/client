package com.lulobank.clients.sdk.operations.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ClientInformationByIdClientContent {

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
}
