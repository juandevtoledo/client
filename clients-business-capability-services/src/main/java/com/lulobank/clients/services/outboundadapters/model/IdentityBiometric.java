package com.lulobank.clients.services.outboundadapters.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@DynamoDBDocument
public class IdentityBiometric {
  private String idTransaction;
  private String status;
  private TransactionState transactionState;
}
