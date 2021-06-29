package com.lulobank.clients.services.features.profile.validators;

import com.lulobank.clients.services.exception.ValidateRequestException;
import com.lulobank.clients.services.features.profile.model.UpdateEmailClientRequest;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.clients.services.utils.LogMessages;
import com.lulobank.clients.services.utils.UtilValidators;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.util.NoSuchElementException;

public class UpdateClientEmailValidator {
  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateClientEmailValidator.class);

  private final ClientsRepository repository;
  private static final String MALFORMED_BODY = "problems validating request";
  private static final String BAD_REQUEST = "request body could not be read";
  private static final String EMPTY_OR_MALFORMED_FIELD = "empty or malformed field";
  private static final String MISSING_FIELD = "missing field";
  private static final String PASSWORD_OR_EMAIL_DOES_NOT_MATCH = "password or email does not match";
  private static final String EMAIL_EXIST_IN_DB = "new email already exists";

  public UpdateEmailClientRequest validate(UpdateEmailClientRequest updateEmailClientRequest) {
    return Try.of(() -> updateEmailClientRequest)
        .map(this::validateFieldsType)
        .map(this::validateNewEmail)
        .map(this::validateDynamoClient)
        .onFailure(ValidateRequestException.class, e -> {throw e;})
        .onFailure(Exception.class, e -> {
          LOGGER.error(LogMessages.INVALID_REQUEST_REASONS.getMessage(), e.getMessage());
          throw new ValidateRequestException(MALFORMED_BODY, HttpStatus.BAD_REQUEST.value());
        })
        .get();
  }

  public UpdateClientEmailValidator(ClientsRepository repository) {
    this.repository = repository;
  }

  private UpdateEmailClientRequest validateFieldsType(UpdateEmailClientRequest updateEmailClientRequest) {
    return Option.of(updateEmailClientRequest).toTry()
        .map(updateEmailRequest -> {
          Validate.notNull(updateEmailRequest);
          Validate.notEmpty(updateEmailRequest.getIdClient());
          Validate.notEmpty(updateEmailRequest.getNewEmail());
          Validate.notEmpty(updateEmailRequest.getOldEmail());
          Validate.notEmpty(updateEmailRequest.getPassword());
          Validate.isTrue(UtilValidators.validateEmail(updateEmailRequest.getNewEmail()));
          return updateEmailRequest;
        }).onFailure(NoSuchElementException.class, e -> {
          throw new ValidateRequestException(BAD_REQUEST, HttpStatus.BAD_REQUEST.value());
        }).onFailure(NullPointerException.class, e -> {
          throw new ValidateRequestException(MISSING_FIELD, HttpStatus.BAD_REQUEST.value());
        }).onFailure(IllegalArgumentException.class, e -> {
          throw new ValidateRequestException(EMPTY_OR_MALFORMED_FIELD, HttpStatus.BAD_REQUEST.value());
        }).get();
  }

  private UpdateEmailClientRequest validateNewEmail(UpdateEmailClientRequest updateEmailRequest) {
    return Option.of(repository.findByEmailAddress(updateEmailRequest.getNewEmail()))
        .map(e -> {
          throw new ValidateRequestException(EMAIL_EXIST_IN_DB, HttpStatus.PRECONDITION_FAILED.value());
        })
        .transform(e -> updateEmailRequest);
  }

  public UpdateEmailClientRequest validateDynamoClient(UpdateEmailClientRequest updateEmailRequest) {
    return Option.ofOptional(repository.findByIdClient(updateEmailRequest.getIdClient()))
        .filter(dbClient -> dbClient.getEmailAddress().equalsIgnoreCase(updateEmailRequest.getOldEmail()))
        .map(request -> updateEmailRequest)
        .getOrElseThrow(() -> new ValidateRequestException(
            PASSWORD_OR_EMAIL_DOES_NOT_MATCH, HttpStatus.PRECONDITION_FAILED.value()
        ));
  }

}
