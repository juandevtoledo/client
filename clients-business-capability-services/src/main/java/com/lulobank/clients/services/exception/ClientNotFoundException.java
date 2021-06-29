package com.lulobank.clients.services.exception;

public class ClientNotFoundException extends RuntimeException {
  private String idClient;

  public ClientNotFoundException() {}

  public ClientNotFoundException(String message) {
    super(message);
  }

  public ClientNotFoundException(String message, String idClient) {
    super(message);
    this.idClient = idClient;
  }

  public ClientNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public ClientNotFoundException(Throwable cause) {
    super(cause);
  }

  public String getIdClient() {
    return idClient;
  }
}
