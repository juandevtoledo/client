package com.lulobank.clients.services.ports.out;


import com.lulobank.parameters.sdk.dto.parameters.ParameterResponse;
import io.vavr.control.Try;

import java.util.Map;

public interface ParameterService {

    Try<ParameterResponse> getParameterByKey(Map<String, String> headers, String clientId, String key);

}
