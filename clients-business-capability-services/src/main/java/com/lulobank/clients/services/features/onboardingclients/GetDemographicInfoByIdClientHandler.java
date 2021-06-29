package com.lulobank.clients.services.features.onboardingclients;

import static com.lulobank.clients.services.utils.ClientErrorResultsEnum.CLIENT_DB_ERROR;
import static com.lulobank.clients.services.utils.ClientErrorResultsEnum.CLIENT_NOT_FOUND_IN_DB;

import com.amazonaws.SdkClientException;
import com.lulobank.clients.sdk.operations.dto.DemographicInfoByIdClient;
import com.lulobank.clients.services.features.onboardingclients.model.GetDemographicInfoByIdClient;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.clients.services.utils.DemographicInformationMapper;
import com.lulobank.clients.services.utils.HttpCodes;
import com.lulobank.clients.services.utils.LogMessages;
import com.lulobank.core.Handler;
import com.lulobank.core.Response;
import com.lulobank.core.utils.ValidatorUtils;
import org.jetbrains.annotations.NotNull;
import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @deprecated (see ClientsDemographicAdapterV3
 * new url /api/v3/client/{idClient}/demographic) 
 */
@Deprecated
public class GetDemographicInfoByIdClientHandler
    implements Handler<Response<DemographicInfoByIdClient>, GetDemographicInfoByIdClient> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(GetDemographicInfoByIdClientHandler.class);

  private ClientsRepository repository;

  public GetDemographicInfoByIdClientHandler(ClientsRepository repository) {
    this.repository = repository;
  }

  @Override
  public Response<DemographicInfoByIdClient> handle(
      GetDemographicInfoByIdClient getDemographicInfoByIdClient) {
    Response<DemographicInfoByIdClient> response;
    try {
      response =
          repository
              .findByIdClient(getDemographicInfoByIdClient.getIdClient())
              .map(clientEntity -> new Response<>(DemographicInformationMapper.from(clientEntity)))
              .orElseGet(() -> getResponseClientNotFound(getDemographicInfoByIdClient));
    } catch (SdkClientException e) {
      response =
          new Response<>(
              ValidatorUtils.getListValidations(
                  CLIENT_DB_ERROR.name(), HttpCodes.INTERNAL_SERVER_ERROR));
    }
    return response;
  }

  @NotNull
  private Response<DemographicInfoByIdClient> getResponseClientNotFound(
      GetDemographicInfoByIdClient getDemographicInfoByIdClient) {
    LOGGER.error(
        LogMessages.CLIENT_NOT_FOUND_GETTING_DEMOGRAPHIC_INFO.getMessage(),
            Encode.forJava(getDemographicInfoByIdClient.getIdClient()));
    return new Response<>(
        ValidatorUtils.getListValidations(CLIENT_NOT_FOUND_IN_DB.name(), HttpCodes.NOT_FOUND));
  }
}
