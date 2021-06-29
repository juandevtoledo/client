package com.lulobank.clients.services.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Client {

  private String id;
  private String idCard;
  private String name;
  private String lastName;
  private String address;
  private Email email;
  private String qualityCode;
  private Phone phone;
  private List<Attachment> attachments;
  private String idCognito;
  private String idCbs;
  private BlackListState blackListState;
  private LocalDate dateOfIssue;
  private List<ForeignTransaction> foreignTransactions;
  private String idApplicant;

  Client() {
    this.attachments = new ArrayList<>();
    this.foreignTransactions = new ArrayList<>();
  }

  public Boolean isConfirmedUser() {
    return (hasId() && hasBasicInformation());
  }

  private boolean hasBasicInformation() {
    return !Objects.isNull(dateOfIssue) && !Objects.isNull(email) && !Objects.isNull(phone);
  }

  private boolean hasId() {
    return !Objects.isNull(id) && !Objects.isNull(idCard) && !Objects.isNull(idCognito);
  }
}
