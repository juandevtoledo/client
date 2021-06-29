package com.lulobank.clients.services.domain;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import lombok.Getter;

@Getter
public class Attachment extends ValueObject<Attachment> {
  private String key;
  private String link;

  public Attachment(String key, String link) {
    this.key = key;
    this.link = link;
  }

  @SuppressWarnings("rawtypes")
  @Override
  protected List<Supplier> supplyGettersToIncludeInEqualityCheck() {
    return Arrays.asList(this::getKey, this::getLink);
  }
}
