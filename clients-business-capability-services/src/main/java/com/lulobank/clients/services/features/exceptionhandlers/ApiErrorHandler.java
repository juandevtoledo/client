package com.lulobank.clients.services.features.exceptionhandlers;

import com.lulobank.biometric.api.exception.MFAUnauthorizedException;
import com.lulobank.clients.sdk.operations.dto.ClientErrorResult;
import com.lulobank.clients.services.exception.ClientNotFoundException;
import com.lulobank.clients.services.utils.LogMessages;
import lombok.extern.slf4j.Slf4j;
import org.owasp.encoder.Encode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static com.lulobank.core.utils.ValidatorUtils.getListValidations;
import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
@Slf4j
public class ApiErrorHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ClientErrorResult> handlerException(Exception ex) {
    log.error(
        LogMessages.SERVICE_EXCEPTION.getMessage(),
        ex.getMessage(),
        INTERNAL_SERVER_ERROR.value(),
        ex);

    ClientErrorResult clientErrorResult =
        new ClientErrorResult(
            getListValidations(
                INTERNAL_SERVER_ERROR.name(), String.valueOf(INTERNAL_SERVER_ERROR.value())));

    return new ResponseEntity<>(clientErrorResult, INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(ClientNotFoundException.class)
  public ResponseEntity<ClientErrorResult> handlerClientNotFoundException(
      ClientNotFoundException ex) {
    log.error(
        LogMessages.CLIENT_NOT_FOUND_IN_DB_EXCEPTION.getMessage(), Encode.forJava(ex.getIdClient()), ex);

    ClientErrorResult clientErrorResult =
        new ClientErrorResult(
            getListValidations(ex.getMessage(), String.valueOf(NOT_FOUND.value())));

    return new ResponseEntity<>(clientErrorResult, NOT_FOUND);
  }

  @ExceptionHandler(MFAUnauthorizedException.class)
  public ResponseEntity<ErrorHandler> handlerRunTimeException(MFAUnauthorizedException ex) {
    ErrorHandler error = getErrorHandler(ex.getDetail());
    return ResponseEntity
            .status(error.getCode())
            .body(error);
  }

  private ErrorHandler getErrorHandler(String detail) {
    return ErrorHandler.builder()
            .failure(detail)
            .code(NOT_ACCEPTABLE.value())
            .detail("Unauthorized token")
            .build();
  }
}
