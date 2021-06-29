package com.lulobank.clients.services.outboundadapters.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.lulobank.clients.services.outboundadapters.dynamoconverter.LocalDateTimeConverter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@DynamoDBDocument
@NoArgsConstructor
public class AttemptEntity {
  private LocalDateTime attemptDate;
  private Double penalty;
  private Boolean maxAttempt;

  public AttemptEntity(Double penalty, Boolean maxAttempt) {
    this.penalty = penalty;
    this.attemptDate = LocalDateTime.now(ZoneId.of("America/Bogota"));
    this.maxAttempt = maxAttempt;
  }

  @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
  public LocalDateTime getAttemptDate() {
    return attemptDate;
  }
}
