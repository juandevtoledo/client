package com.lulobank.clients.sdk.operations.util;

import com.lulobank.utils.exception.ServiceException;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import retrofit2.Response;

public class RetrofitsUtil<T> {

  private Integer statusCode;

  public RetrofitsUtil() {
    this.statusCode = HttpStatus.OK.value();
  }

  public ResponseEntity<T> getResponseEntity(Response<T> response) throws IOException {

    if (response.code() != statusCode) {
      String errorBody = response.errorBody() != null ? response.errorBody().string() : "";
      throw new ServiceException(response.code(), errorBody);
    }
    return new ResponseEntity<>(response.body(), HttpStatus.valueOf(response.code()));
  }
}
