package com.lulobank.clients.services.utils;

import com.lulobank.clients.sdk.operations.dto.ClientErrorResult;
import com.lulobank.core.Response;
import com.lulobank.core.utils.ValidatorUtils;
import com.lulobank.core.validations.ValidationResult;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.Optional;

import static com.lulobank.clients.services.utils.HttpCodes.BAD_REQUEST;
import static com.lulobank.clients.services.utils.HttpCodes.INTERNAL_SERVER_ERROR;
import static com.lulobank.core.utils.ValidatorUtils.getHttpStatusByCode;

public class ExceptionUtils {

  public static Response getResponseBindingResult(BindingResult bindingResult) {
    return new Response(ValidatorUtils.getListValidations(getErrorBindingResult(bindingResult), BAD_REQUEST));
  }

  public static final ResponseEntity getResponseEntityError(Response response) {
    String httpStatus =
        ((Optional<ValidationResult>) response.getErrors().stream().findFirst())
            .map(error -> error.getValue())
            .orElse(INTERNAL_SERVER_ERROR);
    return new ResponseEntity<>(
        new ClientErrorResult(response.getErrors()), getHttpStatusByCode(httpStatus));
  }

  public static String getErrorBindingResult(BindingResult bindingResult){
    return  bindingResult.getAllErrors().stream()
            .findFirst()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .orElse("Unknown error");

  }
}
