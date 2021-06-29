package com.lulobank.clients.services.features.clientproducts.validators;

import static com.lulobank.clients.services.utils.UtilValidators.getValidationResult;
import static com.lulobank.core.utils.ValidatorUtils.getNotNullValidations;

import com.lulobank.clients.services.features.clientproducts.model.Client;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.clients.services.utils.ClientErrorResultsEnum;
import com.lulobank.core.validations.ValidationResult;
import com.lulobank.core.validations.Validator;
import java.util.Objects;
import java.util.Optional;

public class ClientValidator implements Validator<Client> {

  private ClientsRepository clientsRepository;

  public ClientValidator(ClientsRepository clientsRepository) {
    this.clientsRepository = clientsRepository;
  }

  @Override
  public ValidationResult validate(Client client) {
    ValidationResult validationResult = getNotNullValidations(client);

    if (Objects.isNull(validationResult)) {
      Optional<ClientEntity> clientEntity = clientsRepository.findByIdClient(client.getIdClient());
      if (!clientEntity.isPresent()) {
        validationResult =
            getValidationResult(
                ClientErrorResultsEnum.CLIENT_NOT_FOUND_IN_DB.name(),
                ClientErrorResultsEnum.CLIENT_NOT_FOUND_IN_DB.name());
      }
    }
    return validationResult;
  }
}
