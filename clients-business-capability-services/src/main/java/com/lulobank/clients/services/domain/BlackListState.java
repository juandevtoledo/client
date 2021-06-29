package com.lulobank.clients.services.domain;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import lombok.Getter;

@Getter
public class BlackListState extends ValueObject<BlackListState> {
  private StateBlackList state;
  private LocalDateTime updatedOn;

  public BlackListState(StateBlackList state, LocalDateTime updatedOn) {
    this.updatedOn = updatedOn;
    this.state = state;
  }

  @Override
  protected List<Supplier> supplyGettersToIncludeInEqualityCheck() {
    return Arrays.asList(this::getState);
  }
}
