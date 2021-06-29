package com.lulobank.clients.sdk.operations.dto;

import com.lulobank.core.Command;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetClientInformationByIdCard implements Command {
  private String idCard;

  public GetClientInformationByIdCard(String idCard) {
    this.idCard = idCard;
  }
}
