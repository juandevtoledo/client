package com.lulobank.clients.services.features.initialclient.validators;

import static com.lulobank.core.utils.ValidatorUtils.getNotNullValidations;

import com.lulobank.clients.services.features.initialclient.model.CreateInitialClient;
import com.lulobank.clients.services.features.initialclient.model.PhoneCreateInitialClient;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.clients.services.utils.UtilValidators;
import com.lulobank.core.validations.ValidationResult;
import com.lulobank.core.validations.Validator;
import java.util.Objects;
import org.apache.commons.lang3.Validate;
import org.springframework.http.HttpStatus;

public class CreateInitialClientValidator implements Validator<CreateInitialClient> {
  private static final String BAD_REQUEST = String.valueOf(HttpStatus.BAD_REQUEST.value());
  private ClientsRepository repository;
  private static final String REGEX_EMAIL = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";

  public CreateInitialClientValidator(ClientsRepository repository) {
    this.repository = repository;
  }

  @Override
  public ValidationResult validate(CreateInitialClient createInitialClient) {
    ValidationResult result = getNotNullValidations(createInitialClient);
    if (Objects.nonNull(result)) {
      return result;
    }
    try {
      UtilValidators.validatePrefix(
          String.valueOf(createInitialClient.getPhoneCreateInitialClient().getPrefix()));
      UtilValidators.validatePhoneNumber(
          String.valueOf(createInitialClient.getPhoneCreateInitialClient().getNumber()));
      Validate.isTrue(
          createInitialClient.getEmailCreateClientRequest().getAddress().matches(REGEX_EMAIL),
          "email has a wrong format");
      Validate.isTrue(
          !phoneClientExist(createInitialClient.getPhoneCreateInitialClient()),
          "phone already exist");
      Validate.isTrue(
          !emailClientExist(createInitialClient.getEmailCreateClientRequest().getAddress()),
          "email already exist");
    } catch (NullPointerException | IllegalArgumentException e) {
      return new ValidationResult(e.getMessage(), BAD_REQUEST);
    }
    return null;
  }

  private boolean phoneClientExist(PhoneCreateInitialClient phoneCreateInitialClient) {
    return repository
        .findByPhonePrefixAndPhoneNumber(
            phoneCreateInitialClient.getPrefix(), phoneCreateInitialClient.getNumber())
        .isPresent();
  }

  private boolean emailClientExist(String email) {
    return Objects.nonNull(repository.findByEmailAddress(email));
  }
}
