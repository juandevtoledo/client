package com.lulobank.clients.sdk.operations;

import com.lulobank.clients.sdk.operations.dto.onboardingclients.ClientInformationByIdClient;
import java.util.Map;

public interface IClientInformationOperations {
  ClientInformationByIdClient getAllClientInformationByIdClient(
      Map<String, String> headers, String idClient);
}
