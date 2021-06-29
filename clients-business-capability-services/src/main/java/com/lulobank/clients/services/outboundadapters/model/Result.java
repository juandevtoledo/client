package com.lulobank.clients.services.outboundadapters.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@DynamoDBDocument
public class Result {
  private Float interestRate;
  private Double amount;
  private Integer installments;
  private Double maxAmountInstallment;
  private String type;

  public Result() {}

  public Result(
      Float interestRate,
      Double amount,
      Integer installments,
      Double maxAmountInstallment,
      String type) {
    this.interestRate = interestRate;
    this.amount = amount;
    this.installments = installments;
    this.maxAmountInstallment = maxAmountInstallment;
    this.type = type;
  }
}
