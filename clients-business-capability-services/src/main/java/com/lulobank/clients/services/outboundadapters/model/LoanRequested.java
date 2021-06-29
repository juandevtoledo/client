package com.lulobank.clients.services.outboundadapters.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@DynamoDBDocument
public class LoanRequested {
  private String status;
  private LoanClientRequested loanClientRequested;

  public LoanRequested() {}

  public LoanRequested(String status) {
    this.status = status;
  }
}
