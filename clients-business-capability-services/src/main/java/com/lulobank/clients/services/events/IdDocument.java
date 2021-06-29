package com.lulobank.clients.services.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class IdDocument {
  private String documentType;
  private String idCard;
  private String expeditionDate;
}
