package com.lulobank.clients.services.features.onboardingclients.model;

import com.lulobank.core.Command;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateClientRequest implements Serializable, Command {
  private static final long serialVersionUID = 1L;
  private String idClient;
  private String idCard;
  private String name;
  private String lastName;
  private String address;
  private String password;
  private EmailCreateClientRequest email;
  private PhoneCreateClientRequest phone;
  private List<AttachmentCreateClientRequest> attachments;
  private List<ForeignTransactionCreateClientRequest> foreignCurrencyTransactions;
  private String gender;
  private String idCredit;
  private List<LoanConditionsRequest> loanConditionsList;

  public CreateClientRequest() {
    attachments = new ArrayList<>();
    foreignCurrencyTransactions = new ArrayList<>();
  }
}
