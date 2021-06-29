package com.lulobank.clients.starter.v3.adapters.out.dynamo.dto;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@DynamoDBDocument
public class OnBoardingStatus {
  private String checkpoint;
  private String productSelected;
  private LoanClientRequested loanClientRequested;

  public OnBoardingStatus() {}

  public OnBoardingStatus(String checkpoint, String productSelected) {
    this.checkpoint = checkpoint;
    this.productSelected = productSelected;
  }
}
