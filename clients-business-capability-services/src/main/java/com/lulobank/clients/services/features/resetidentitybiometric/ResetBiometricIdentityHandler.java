package com.lulobank.clients.services.features.resetidentitybiometric;

import static com.lulobank.clients.services.utils.ClientErrorResultsEnum.BIOMETRIC_IDENTITY_NOT_FOUND;
import static com.lulobank.clients.services.utils.ClientErrorResultsEnum.CLIENT_DB_ERROR;
import static com.lulobank.clients.services.utils.ClientHelper.notifyClientOnboarding;
import static com.lulobank.clients.services.utils.LoanRequestedStatus.IN_PROGRESS;
import static com.lulobank.clients.services.utils.LogMessages.DYNAMO_ERROR_EXCEPTION;
import static com.lulobank.core.utils.ValidatorUtils.getListValidations;
import static java.lang.Boolean.TRUE;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.amazonaws.SdkClientException;
import com.lulobank.clients.sdk.operations.dto.resetidentitybiometric.ClientToReset;
import com.lulobank.clients.services.exception.ClientNotFoundException;
import com.lulobank.clients.services.outboundadapters.ClientsOutboundAdapter;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.model.IdentityBiometric;
import com.lulobank.clients.services.utils.LogMessages;
import com.lulobank.core.Handler;
import com.lulobank.core.Response;
import com.lulobank.utils.exception.ServiceException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResetBiometricIdentityHandler implements Handler<Response, ClientToReset> {
  private ClientsOutboundAdapter clientsOutboundAdapter;

  public ResetBiometricIdentityHandler(ClientsOutboundAdapter clientsOutboundAdapter) {
    this.clientsOutboundAdapter = clientsOutboundAdapter;
  }

  @Override
  public Response handle(ClientToReset clientToReset) {

    Response response = null;
    try {
      ClientEntity clientEntity =
          clientsOutboundAdapter
              .getClientsRepository()
              .findByIdClient(clientToReset.getIdClient())
              .orElseThrow(ClientNotFoundException::new);
      IdentityBiometric identityBiometric = getIdentityBiometric(clientEntity);
      identityBiometric.setStatus(IN_PROGRESS.name());
      identityBiometric.setTransactionState(null);
      clientEntity.setIdentityBiometric(identityBiometric);
      clientEntity.setResetBiometric(TRUE);
      clientsOutboundAdapter.getClientsRepository().save(clientEntity);
      notifyClientOnboarding.accept(clientEntity, clientsOutboundAdapter);
      response = new Response(TRUE);
    } catch (ServiceException ex) {
      log.error(LogMessages.SERVICE_EXCEPTION.getMessage(), ex.getMessage(), ex.getCode(), ex);
      response = new Response<>(getListValidations(ex.getMessage(), String.valueOf(ex.getCode())));
    } catch (SdkClientException ex) {
      log.error(DYNAMO_ERROR_EXCEPTION.getMessage(), ex.getMessage(), ex);
      response =
          new Response<>(
              getListValidations(CLIENT_DB_ERROR.name(), String.valueOf(BAD_GATEWAY.value())));
    }
    return response;
  }

  private IdentityBiometric getIdentityBiometric(ClientEntity clientEntity) {
    return Optional.ofNullable(clientEntity.getIdentityBiometric())
        .orElseThrow(
            () -> new ServiceException(NOT_FOUND.value(), BIOMETRIC_IDENTITY_NOT_FOUND.name()));
  }
}
