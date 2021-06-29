package com.lulobank.clients.services.features.profile.validators;

import com.lulobank.clients.services.exception.ValidateRequestException;
import com.lulobank.clients.services.features.profile.model.UpdateEmailClientRequest;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.clients.services.utils.ClientErrorResultsEnum;
import com.lulobank.core.validations.ValidationResult;
import com.lulobank.core.validations.Validator;
import org.apache.commons.lang3.Validate;
import org.springframework.http.HttpStatus;

/**
 * @deprecated due to security issues. Remove once the <tt>/clients/profile/email/update</tt> endpoint has been completely unused
 */
@Deprecated
public class UpdateEmailClientValidator implements Validator<UpdateEmailClientRequest> {
  private ClientsRepository repository;
  private static final String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";

  public UpdateEmailClientValidator(ClientsRepository repository) {
    this.repository = repository;
  }

  @Override
  public ValidationResult validate(UpdateEmailClientRequest updateEmailClientRequest) {
    try {
      Validate.notNull(updateEmailClientRequest);
      Validate.notEmpty(updateEmailClientRequest.getIdClient());
      Validate.notEmpty(updateEmailClientRequest.getNewEmail());
      Validate.notEmpty(updateEmailClientRequest.getOldEmail());
      Validate.notEmpty(updateEmailClientRequest.getPassword());
      Validate.isTrue(updateEmailClientRequest.getNewEmail().matches(EMAIL_REGEX));
      emailClientExist(updateEmailClientRequest.getNewEmail());
    } catch (ValidateRequestException e) {
      return new ValidationResult(
          ClientErrorResultsEnum.EMAIL_EXIST_IN_DB.name(),
          String.valueOf(HttpStatus.PRECONDITION_FAILED.value()));
    } catch (Exception e) {
      return new ValidationResult(
          ClientErrorResultsEnum.VALIDATION_ERROR.name(),
          String.valueOf(HttpStatus.BAD_REQUEST.value()));
    }
    return null;
  }

  private void emailClientExist(String email) {
    if (repository.findByEmailAddress(email) != null) {
      throw new ValidateRequestException();
    }
  }
}
