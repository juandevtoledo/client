package com.lulobank.clients.services.features.onboardingclients;

import static com.lulobank.clients.services.utils.ClientErrorResultsEnum.TYPE_SEARCH_ERROR;
import static com.lulobank.clients.services.utils.ClientErrorResultsEnum.TYPE_SEARCH_NOT_FOUND;

import com.lulobank.clients.sdk.operations.dto.ClientInformationByTypeResponse;
import com.lulobank.clients.services.features.onboardingclients.model.ClientInformationByTypeRequest;
import com.lulobank.clients.services.features.onboardingclients.model.TypeSearch;
import com.lulobank.clients.services.features.onboardingclients.model.TypeSearchFactory;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.clients.services.utils.HttpCodes;
import com.lulobank.core.Handler;
import com.lulobank.core.Response;
import com.lulobank.core.utils.ValidatorUtils;
import io.vavr.control.Option;
import io.vavr.control.Try;
import java.util.Optional;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetClientInformationByTypeHandler
    implements Handler<Response<ClientInformationByTypeResponse>, ClientInformationByTypeRequest> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(GetClientInformationByTypeHandler.class);

  private ClientsRepository repository;

  public GetClientInformationByTypeHandler(ClientsRepository repository) {
    this.repository = repository;
  }

  @Override
  public Response<ClientInformationByTypeResponse> handle(
      ClientInformationByTypeRequest clientInformationByTypeRequest) {
    return Try.of(
            () ->
                Option.of(
                        TypeSearchFactory.getTypeSearch(
                            clientInformationByTypeRequest.getSearchType()))
                    .map(getTypeSearch(clientInformationByTypeRequest))
                    .getOrElse(this::typeSearchNotFound))
        .recover(Exception.class, handleAnyException())
        .get();
  }

  private Response<ClientInformationByTypeResponse> typeSearchNotFound() {
    return new Response<>(
        ValidatorUtils.getListValidations(TYPE_SEARCH_NOT_FOUND.name(), HttpCodes.NOT_FOUND));
  }

  private Function<Exception, Response<ClientInformationByTypeResponse>> handleAnyException() {
    return e -> {
      LOGGER.error(HttpCodes.INTERNAL_SERVER_ERROR, e.getMessage(), e);
      return new Response<>(
          ValidatorUtils.getListValidations(
              TYPE_SEARCH_ERROR.name(), HttpCodes.INTERNAL_SERVER_ERROR));
    };
  }

  private Function<Optional<TypeSearch>, Response<ClientInformationByTypeResponse>> getTypeSearch(
      ClientInformationByTypeRequest clientInformationByTypeRequest) {
    return typeSearch ->
        typeSearch.get().apply(repository, clientInformationByTypeRequest.getValue());
  }
}
