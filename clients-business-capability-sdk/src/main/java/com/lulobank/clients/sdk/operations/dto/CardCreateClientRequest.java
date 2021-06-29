package com.lulobank.clients.sdk.operations.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardCreateClientRequest {
  private static final String DEFAULT_DOCUMENT_TYPE = "CC";
  private String type = DEFAULT_DOCUMENT_TYPE;
  private String id;
  private String issueDate;

  public CardCreateClientRequest() {}

  public CardCreateClientRequest(String type, String id, String issueDate) {
    this.type = type;
    this.id = id;
    this.issueDate = issueDate;
  }
}
