package com.lulobank.clients.starter.v3.adapters.out.dynamo.dto;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@DynamoDBDocument
public class CreditRiskAnalysis {
  private String status;
  private List<Result> results;

  public CreditRiskAnalysis() {}
}
