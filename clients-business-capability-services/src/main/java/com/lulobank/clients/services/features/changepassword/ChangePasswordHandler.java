package com.lulobank.clients.services.features.changepassword;

import static com.lulobank.clients.services.utils.ClientErrorResultsEnum.CLIENT_NOT_FOUND_IN_DB;
import static com.lulobank.clients.services.utils.ClientErrorResultsEnum.CLIENT_UPDATE_ERROR;
import static com.lulobank.core.utils.ValidatorUtils.getListValidations;

import com.amazonaws.services.cognitoidp.model.ChangePasswordRequest;
import com.amazonaws.services.cognitoidp.model.ChangePasswordResult;
import com.lulobank.clients.services.features.changepassword.model.NewPasswordRequest;
import com.lulobank.clients.services.features.changepassword.model.Password;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.clients.services.utils.CognitoProperties;
import com.lulobank.core.Handler;
import com.lulobank.core.Response;
import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class ChangePasswordHandler implements Handler<Response<Password>, NewPasswordRequest> {

  private final CognitoProperties cognitoProperties;
  private final ClientsRepository clientsRepository;
  private static final Logger logger = LoggerFactory.getLogger(ChangePasswordHandler.class);

  public ChangePasswordHandler(
      CognitoProperties cognitoProperties, ClientsRepository clientsRepository) {
    this.cognitoProperties = cognitoProperties;
    this.clientsRepository = clientsRepository;
  }

  @Override
  public Response<Password> handle(NewPasswordRequest password) {
    try {
      ClientEntity clientEntity =
          clientsRepository.findByIdClient(password.getIdClient()).orElse(null);
      if (clientEntity != null) {
        return new Response<>(updatePasswordDynamoUser(password));
      } else {
        logger.error(CLIENT_NOT_FOUND_IN_DB.name());
        return new Response<>(
            getListValidations(
                CLIENT_NOT_FOUND_IN_DB.name(), String.valueOf(HttpStatus.NOT_FOUND.value())));
      }
    } catch (Exception e) {
      logger.error(CLIENT_UPDATE_ERROR.name(), e);
      return new Response<>(
          getListValidations(
              CLIENT_UPDATE_ERROR.name(),
              String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())));
    }
  }

  private Password updatePasswordDynamoUser(NewPasswordRequest passwordRequest) {

    ChangePasswordRequest request = new ChangePasswordRequest();
    request.setAccessToken(passwordRequest.getAccessToken());
    request.setProposedPassword(passwordRequest.getNewPassword());
    request.setPreviousPassword(passwordRequest.getOldPassword());

    ChangePasswordResult result =
        cognitoProperties.getAwsCognitoIdentityProvider().changePassword(request);
    logger.info(
        "Password updated in Cognito for " + "IdClient : {} {}",
        Encode.forJava(passwordRequest.getIdClient()),
        result.toString());

    Password password = new Password();
    password.setIdClient(passwordRequest.getIdClient());
    return password;
  }
}
