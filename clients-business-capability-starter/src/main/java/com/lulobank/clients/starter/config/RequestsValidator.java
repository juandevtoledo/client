package com.lulobank.clients.starter.config;

import com.lulobank.authentication.sdk.dto.AuthenticationFailure;
import com.lulobank.authentication.sdk.dto.AuthenticationFailureResult;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RequestsValidator {

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public AuthenticationFailureResult<AuthenticationFailure> handleValidationExceptions(MethodArgumentNotValidException manvex) {
    AuthenticationFailure[] errors = manvex.getBindingResult().getAllErrors()
        .stream()
        .map(ObjectError::getDefaultMessage)
        .map((String failure) -> new AuthenticationFailure(failure, HttpStatus.BAD_REQUEST.value()))
        .toArray(AuthenticationFailure[]::new);
    return new AuthenticationFailureResult<>(errors);
  }
}
