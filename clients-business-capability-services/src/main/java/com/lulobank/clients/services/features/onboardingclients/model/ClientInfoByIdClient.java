package com.lulobank.clients.services.features.onboardingclients.model;

import static com.lulobank.clients.services.utils.ClientErrorResultsEnum.CLIENT_DB_ERROR;
import static com.lulobank.clients.services.utils.ClientErrorResultsEnum.CLIENT_NOT_FOUND_IN_DB;

import com.lulobank.clients.sdk.operations.dto.ClientInformationByTypeResponse;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.clients.services.utils.HttpCodes;
import com.lulobank.clients.services.utils.LogMessages;
import com.lulobank.core.Response;
import com.lulobank.core.utils.ValidatorUtils;
import io.vavr.control.Option;
import io.vavr.control.Try;
import java.util.Optional;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientInfoByIdClient implements TypeSearch {

  private static final Logger LOGGER = LoggerFactory.getLogger(ClientInfoByIdClient.class);

  @Override
  public Response<ClientInformationByTypeResponse> apply(
      ClientsRepository repository, String value) {
    return Try.of(
            () ->
                Option.of(repository.findByIdClient(value))
                    .map(getClientEntity())
                    .getOrElse(this::clientNotFound))
        .recover(Exception.class, handleAnyException())
        .get();
  }

  private Function<Exception, Response<ClientInformationByTypeResponse>> handleAnyException() {
    return e -> {
      LOGGER.error(HttpCodes.INTERNAL_SERVER_ERROR, e.getMessage(), e);
      return new Response<>(
          ValidatorUtils.getListValidations(
              CLIENT_DB_ERROR.name(), HttpCodes.INTERNAL_SERVER_ERROR));
    };
  }

  private Response<ClientInformationByTypeResponse> clientNotFound() {
    LOGGER.error(LogMessages.CLIENT_NOT_FOUND_IN_DB_EXCEPTION.getMessage(), HttpCodes.NOT_FOUND);
    return new Response<>(
        ValidatorUtils.getListValidations(CLIENT_NOT_FOUND_IN_DB.name(), HttpCodes.NOT_FOUND));
  }

  private Function<Optional<ClientEntity>, Response<ClientInformationByTypeResponse>>
      getClientEntity() {
    return p ->
        new Response<>(ClientInfoEntityMapper.INSTANCE.createClientEntityToClientInfo(p.get()));
  }
}
