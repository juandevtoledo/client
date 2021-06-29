package com.lulobank.clients.services.features.productsloanrequested;

import org.springframework.http.HttpHeaders;

import com.lulobank.core.Command;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductsLoanRequestedWithClient implements Command {

  private String idClient;
  private HttpHeaders headers;

  public ProductsLoanRequestedWithClient(String idClient, HttpHeaders headers) {
    this.idClient = idClient;
    this.headers = headers;
  }
}
