package com.lulobank.clients.starter.v3.adapters.out.dynamo.dto;

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
