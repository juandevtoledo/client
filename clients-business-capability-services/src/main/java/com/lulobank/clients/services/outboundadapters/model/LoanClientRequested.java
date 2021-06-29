package com.lulobank.clients.services.outboundadapters.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@DynamoDBDocument
public class LoanClientRequested {
  private Double amount;
  private String loanPurpose;

  public LoanClientRequested() {}

  public LoanClientRequested(Double amount, String loanPurpose) {
    this.amount = amount;
    this.loanPurpose = loanPurpose;
  }
}
