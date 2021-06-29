package com.lulobank.clients.services.features.onboardingvalidations;

import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.core.Handler;
import com.lulobank.core.Response;
import com.lulobank.core.helpers.ObjectHelpers;
import org.apache.commons.lang3.LocaleUtils;

public class ValidateEmailExistanceHandler
    implements Handler<Response<ValidateExistenceResult>, ValidateExistenceRequest> {

  private ClientsRepository repository;

  public ValidateEmailExistanceHandler(ClientsRepository repository) {
    this.repository = repository;
  }

  @Override
  public Response<ValidateExistenceResult> handle(
      ValidateExistenceRequest validateExistenceRequest) {
    ClientEntity clientEntity =
        repository.findByEmailAddress(validateExistenceRequest.getPropertie().toLowerCase(LocaleUtils.toLocale("es_CO")));
    return new Response<>(new ValidateExistenceResult(!ObjectHelpers.isNull(clientEntity)));
  }
}