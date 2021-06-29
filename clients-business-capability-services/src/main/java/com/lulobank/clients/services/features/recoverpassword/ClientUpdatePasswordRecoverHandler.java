package com.lulobank.clients.services.features.recoverpassword;

import static com.lulobank.clients.services.utils.CognitoHelper.recoverPasswordClient;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.cognitoidp.model.InvalidPasswordException;
import com.amazonaws.services.cognitoidp.model.UserNotFoundException;
import com.lulobank.clients.services.exception.ClientNotFoundException;
import com.lulobank.clients.services.features.recoverpassword.model.RecoverPasswordUpdate;
import com.lulobank.clients.services.features.recoverpassword.model.RecoverPasswordUpdated;
import com.lulobank.clients.services.outboundadapters.ClientsOutboundAdapter;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.utils.ClientErrorResultsEnum;
import com.lulobank.clients.services.utils.ClientHelper;
import com.lulobank.clients.services.utils.LogMessages;
import com.lulobank.core.Handler;
import com.lulobank.core.Response;
import com.lulobank.core.utils.ValidatorUtils;
import com.lulobank.utils.exception.ServiceException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class ClientUpdatePasswordRecoverHandler
    implements Handler<Response<RecoverPasswordUpdated>, RecoverPasswordUpdate> {

  private ClientsOutboundAdapter clientsOutboundAdapter;
  private static final Logger LOGGER =
      LoggerFactory.getLogger(ClientUpdatePasswordRecoverHandler.class);

  public ClientUpdatePasswordRecoverHandler(ClientsOutboundAdapter clientsOutboundAdapter) {
    this.clientsOutboundAdapter = clientsOutboundAdapter;
  }

  @Override
  public Response<RecoverPasswordUpdated> handle(RecoverPasswordUpdate recoverPasswordUpdate) {
    Response response;
    try {
      Optional<ClientEntity> clientEntityOptional =
          clientsOutboundAdapter
              .getClientsRepository()
              .findByIdCardAndEmailAddress(
                  recoverPasswordUpdate.getIdCard(), recoverPasswordUpdate.getEmailAddress());
      response =
          clientEntityOptional
              .map(
                  clientEntity -> {
                    if (validateOtpEmail(recoverPasswordUpdate)) {
                      recoverPasswordClient.accept(clientsOutboundAdapter, recoverPasswordUpdate);
                      clientEntity.setQualityCode(
                          ClientHelper.getHashString(recoverPasswordUpdate.getNewPassword()));
                      clientsOutboundAdapter.getClientsRepository().save(clientEntity);
                      return new Response<>(new RecoverPasswordUpdated(true));
                    } else {
                      return new Response<>(
                          ValidatorUtils.getListValidations(
                              ClientErrorResultsEnum.INVALID_CODE.name(),
                              String.valueOf(HttpStatus.NOT_ACCEPTABLE.value())));
                    }
                  })
              .orElseThrow(
                  () ->
                      new ClientNotFoundException(
                          ClientErrorResultsEnum.CLIENT_NOT_FOUND_IN_DB.name(),
                          recoverPasswordUpdate.getIdCard()));
    } catch (InvalidPasswordException e) {
      LOGGER.error(LogMessages.COGNITO_ERROR_EXCEPTION.getMessage(), e.getMessage(), e);
      response =
          new Response<>(
              ValidatorUtils.getListValidations(
                  ClientErrorResultsEnum.INVALID_PASSWORD.name(),
                  String.valueOf(HttpStatus.BAD_REQUEST.value())));
    } catch (UserNotFoundException e) {
      LOGGER.error(LogMessages.CLIENT_NOT_FOUND_IN_DB_EXCEPTION.getMessage(), e.getMessage(), e);
      response =
          new Response<>(
              ValidatorUtils.getListValidations(
                  ClientErrorResultsEnum.CLIENT_NOT_FOUND_IN_DB.name(),
                  String.valueOf(HttpStatus.NOT_FOUND.value())));
    } catch (SdkClientException | ServiceException e) {
      LOGGER.error(LogMessages.DYNAMO_ERROR_EXCEPTION.getMessage(), e.getMessage(), e);
      response =
          new Response<>(
              ValidatorUtils.getListValidations(
                  ClientErrorResultsEnum.INTERNAL_SERVER_ERROR.name(),
                  String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())));
    }
    return response;
  }

  private boolean validateOtpEmail(RecoverPasswordUpdate recoverPasswordUpdate) {
    return clientsOutboundAdapter
        .getRetrofitOtpOperations()
        .validateEmailOtp(
            recoverPasswordUpdate.getAuthorizationHeader(),
            recoverPasswordUpdate.getEmailAddress(),
            recoverPasswordUpdate.getVerificationCode());
  }
}
