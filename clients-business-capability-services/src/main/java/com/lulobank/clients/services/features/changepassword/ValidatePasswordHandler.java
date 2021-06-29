package com.lulobank.clients.services.features.changepassword;

import com.lulobank.clients.services.features.changepassword.model.Password;
import com.lulobank.core.Handler;
import com.lulobank.core.Response;

public class ValidatePasswordHandler implements Handler<Response<Password>, Password> {

  @Override
  public Response<Password> handle(Password password) {
    return new Response<>(password);
  }
}
