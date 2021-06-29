package com.lulobank.clients.services.features.onboardingchangeproduct;

import static com.lulobank.clients.services.utils.CheckPointsHelper.setCheckpointSavingAccountCreated;
import static com.lulobank.clients.services.utils.ClientHelper.createSavingAccount;
import static com.lulobank.clients.services.utils.ClientHelper.setOnboardingStatusToClientSaving;
import static com.lulobank.core.utils.ValidatorUtils.getListValidations;

import com.amazonaws.SdkClientException;
import com.lulobank.clients.sdk.operations.dto.onboarding.changeproduct.ChangeOnBoardingSelectedProductToSavingsForClient;
import com.lulobank.clients.services.exception.ClientNotFoundException;
import com.lulobank.clients.services.outboundadapters.ClientsOutboundAdapter;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.utils.ClientErrorResultsEnum;
import com.lulobank.clients.services.utils.LogMessages;
import com.lulobank.core.Handler;
import com.lulobank.core.Response;
import com.lulobank.utils.exception.ServiceException;
import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class ChangeOnBoardingSelectedProductToSavingsForClientHandler
        implements Handler<Response, ChangeOnBoardingSelectedProductToSavingsForClient> {
  private static final Logger LOGGER =
          LoggerFactory.getLogger(ChangeOnBoardingSelectedProductToSavingsForClientHandler.class);
  private ClientsOutboundAdapter clientsOutboundAdapter;

  public ChangeOnBoardingSelectedProductToSavingsForClientHandler(
          ClientsOutboundAdapter clientsOutboundAdapter) {
    this.clientsOutboundAdapter = clientsOutboundAdapter;
  }

  @Override
  public Response handle(ChangeOnBoardingSelectedProductToSavingsForClient request) {
    try {

      ClientEntity clientEntity =
              clientsOutboundAdapter
                      .getClientsRepository()
                      .findByIdClient(request.getIdClient())
                      .orElseThrow(
                              () ->
                                      new ClientNotFoundException(
                                              ClientErrorResultsEnum.CLIENT_NOT_FOUND_IN_DB.name(),
                                              request.getIdClient()));

      setOnboardingStatusToClientSaving.accept(clientEntity);
      createSavingAccount(request.getAuthorizationHeader(), clientEntity, clientsOutboundAdapter);
      setCheckpointSavingAccountCreated.accept(clientEntity);
      clientsOutboundAdapter.getClientsRepository().save(clientEntity);
      return new Response<>(true);
    } catch (ServiceException e) {
      LOGGER.error(LogMessages.SERVICE_EXCEPTION.getMessage(), e, e.getCode());
      return new Response<>(getListValidations(e.getMessage(), String.valueOf(e.getCode())));
    } catch (SdkClientException e) {
      LOGGER.error(LogMessages.DYNAMO_ERROR_EXCEPTION.getMessage(), Encode.forJava(request.getIdClient()));
      return new Response<>(
              getListValidations(
                      e.getMessage(), String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())));
    }
  }
}