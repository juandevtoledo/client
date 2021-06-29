package com.lulobank.clients.starter.v3.adapters.out.dynamo.dto;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@DynamoDBDocument
public class Attachment {
  private String key;
  private String link;

  public Attachment() {
    // Empty constructor required for mapping in Jackson
  }
}
