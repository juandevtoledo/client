package com.lulobank.clients.services.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ClientVerificationFirebase {
  private String productSelected;
  private String verificationResult;
  private String detail;

  public ClientVerificationFirebase(String productSelected, String verificationResult) {
    this.productSelected = productSelected;
    this.verificationResult = verificationResult;
  }
}
