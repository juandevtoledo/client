package com.lulobank.clients.services.outboundadapters.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@DynamoDBDocument
public class CreditRiskAnalysis {
  private String status;
  private List<Result> results;

  public CreditRiskAnalysis() {}
}
