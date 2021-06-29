package com.lulobank.clients.sdk.operations.dto.exception;

import com.lulobank.utils.exception.ServiceException;

public class GetClientInfoException extends ServiceException {
  public GetClientInfoException(String message) {
    super(message);
  }

  public GetClientInfoException(int code, String message) {
    super(code, message);
  }

  public GetClientInfoException(int code, String message, Throwable cause) {
    super(code, message, cause);
  }
}
