package com.lulobank.clients.services.features.productsloanrequested;

import static com.lulobank.clients.services.utils.ClientErrorResultsEnum.CLIENT_NOT_FOUND_IN_DB;
import static com.lulobank.clients.services.utils.IdentityBiometricStatus.FINISHED;
import static com.lulobank.core.utils.ValidatorUtils.getNotNullValidations;
import static java.util.UUID.fromString;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.lulobank.clients.services.exception.ClientNotFoundException;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.clients.services.utils.UtilValidators;
import com.lulobank.core.validations.ValidationResult;
import com.lulobank.core.validations.Validator;
import java.util.Objects;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

public class ProductsLoanRequestedValidator implements Validator<ProductsLoanRequestedWithClient> {
  private final ClientsRepository repository;

  public ProductsLoanRequestedValidator(ClientsRepository repository) {
    this.repository = repository;
  }

  @Override
  public ValidationResult validate(
      ProductsLoanRequestedWithClient productsLoanRequestedWithClient) {

    ValidationResult validationResult = null;
    validationResult = getNotNullValidations(productsLoanRequestedWithClient);

    if (Objects.isNull(validationResult)) {
      try {
        fromString(productsLoanRequestedWithClient.getIdClient());
      } catch (IllegalArgumentException ex) {
        validationResult =
            new ValidationResult("Id Client has wrong format", String.valueOf(BAD_REQUEST.value()));
      }
    }

    if (Objects.isNull(validationResult)) {
      validationResult =
          Optional.ofNullable(
                  validateClientIdentityBiometricStatus(productsLoanRequestedWithClient))
              .orElse(null);
    }

    return validationResult;
  }

  @Nullable
  private ValidationResult validateClientIdentityBiometricStatus(
      ProductsLoanRequestedWithClient productsLoanRequestedWithClient) {
    ClientEntity clientEntity =
        repository
            .findByIdClient(productsLoanRequestedWithClient.getIdClient())
            .orElseThrow(
                () ->
                    new ClientNotFoundException(
                        CLIENT_NOT_FOUND_IN_DB.name(),
                        productsLoanRequestedWithClient.getIdClient()));
    if (Objects.isNull(clientEntity.getIdentityBiometric())
        || !FINISHED.name().equals(clientEntity.getIdentityBiometric().getStatus())) {
      return new ValidationResult(
          "Identity Biometric hasn't been finished yet", String.valueOf(BAD_REQUEST.value()));
    }
    ValidationResult validationResult =
        UtilValidators.getNotNullValidations(productsLoanRequestedWithClient);
    if (Objects.nonNull(validationResult)) {
      return validationResult;
    }

    return null;
  }
}
