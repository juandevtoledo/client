package com.lulobank.clients.services.features.changepassword;

import com.lulobank.clients.services.ILoginAttempts;
import com.lulobank.clients.services.features.changepassword.model.Password;
import com.lulobank.clients.services.features.login.model.AttemptTimeResult;
import com.lulobank.clients.services.outboundadapters.model.AttemptEntity;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.clients.services.utils.ClientErrorResultsEnum;
import com.lulobank.clients.services.utils.ClientHelper;
import com.lulobank.clients.services.utils.LoginErrorResultsEnum;
import com.lulobank.core.utils.ValidatorUtils;
import com.lulobank.core.validations.ValidationResult;
import com.lulobank.core.validations.Validator;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class ValidatePasswordValidator implements Validator<Password> {

  private ClientsRepository clientsRepository;

  private ILoginAttempts loginAttemptsService;

  private static final Logger logger = LoggerFactory.getLogger(ValidatePasswordValidator.class);

  public ValidatePasswordValidator(
      ClientsRepository clientsRepository, ILoginAttempts loginAttemptsService) {
    this.clientsRepository = clientsRepository;
    this.loginAttemptsService = loginAttemptsService;
  }

  @Override
  public ValidationResult validate(Password password) {
    ValidationResult validationResult = ValidatorUtils.getNotNullValidations(password);
    try {

      if (Objects.isNull(validationResult)) {
        Optional<ClientEntity> clientEntity = validatePasswordDynamo(password);

        if (loginAttemptsService.isBlockedLogin(password.getIdClient())) {
          AttemptEntity attemptEntity =
              loginAttemptsService.getLastDateFailedAttempt(password.getIdClient());

          if (attemptEntity.getPenalty() == -1) {
            return new ValidationResult(
                LoginErrorResultsEnum.USER_BLOCKED.name(),
                String.valueOf(HttpStatus.FORBIDDEN.value()));
          } else {
            AttemptTimeResult attemptTimeResult =
                loginAttemptsService.getAttemptTimeFromAttemptEntity(attemptEntity);
            String attemptTimeResultJson =
                loginAttemptsService.getAttemptTimeResult(attemptTimeResult);
            return new ValidationResult(
                LoginErrorResultsEnum.COGNITO_NOT_AUTHORIZED.name(), attemptTimeResultJson);
          }
        }

        if (!clientEntity.isPresent()) {
          AttemptEntity attemptEntity =
              loginAttemptsService.savePasswordAttempt(password.getIdClient(), false);
          AttemptTimeResult attemptTimeResult =
              loginAttemptsService.getAttemptTimeFromAttemptEntity(attemptEntity);
          String attemptTimeResultJson =
              loginAttemptsService.getAttemptTimeResult(attemptTimeResult);
          validationResult =
              new ValidationResult(
                  LoginErrorResultsEnum.COGNITO_NOT_AUTHORIZED.name(), attemptTimeResultJson);
        } else {
          loginAttemptsService.resetFailedAttempts(password.getIdClient());
        }
      } else {
        return validationResult;
      }
    } catch (Exception ex) {
      logger.error("Error validating Password in Dynamo", ex);
      validationResult =
          new ValidationResult(
              ClientErrorResultsEnum.INTERNAL_SERVER_ERROR.name(),
              ClientErrorResultsEnum.INTERNAL_SERVER_ERROR.name());
    }

    return validationResult;
  }

  public Optional<ClientEntity> validatePasswordDynamo(Password password) {
    return clientsRepository.findByIdClientAndEmailAddressAndQualityCode(
        password.getIdClient(),
        password.getEmailAddress(),
        ClientHelper.getHashString(password.getPassword()));
  }
}
