package com.lulobank.clients.services.features.onboardingclients.model;

import com.lulobank.clients.sdk.operations.dto.ClientInformationByTypeResponse;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.core.Response;

public interface TypeSearch {
  Response<ClientInformationByTypeResponse> apply(ClientsRepository repository, String value);
}
