package com.lulobank.clients.services.features.changepassword;

import static com.lulobank.core.utils.ValidatorUtils.getListValidations;

import com.lulobank.clients.services.features.changepassword.model.NewPasswordRequest;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.clients.services.utils.ClientErrorResultsEnum;
import com.lulobank.clients.services.utils.UtilValidators;
import com.lulobank.core.validations.ValidationResult;
import com.lulobank.core.validations.Validator;
import java.util.Objects;
import java.util.Optional;
import org.springframework.http.HttpStatus;

public class ValidateNewPasswordValidator implements Validator<NewPasswordRequest> {

  private ClientsRepository clientsRepository;

  public ValidateNewPasswordValidator(ClientsRepository clientsRepository) {
    this.clientsRepository = clientsRepository;
  }

  @Override
  public ValidationResult validate(NewPasswordRequest password) {
    ValidationResult validationResult = UtilValidators.getNotNullValidations(password);

    if (Objects.isNull(validationResult)) {
      Optional<ClientEntity> clientEntity =
          clientsRepository.findByIdClientAndEmailAddress(
              password.getIdClient(), password.getEmailAddress());

      if (!clientEntity.isPresent()) {
        return getListValidations(
                ClientErrorResultsEnum.CLIENT_NOT_FOUND_IN_DB.name(),
                String.valueOf(HttpStatus.NOT_FOUND.value()))
            .get(0);
      }
    }
    return validationResult;
  }
}
