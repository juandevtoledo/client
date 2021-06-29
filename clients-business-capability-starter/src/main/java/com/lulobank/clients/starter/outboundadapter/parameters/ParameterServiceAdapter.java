package com.lulobank.clients.starter.outboundadapter.parameters;

import com.lulobank.clients.services.exception.ParametersServiceException;
import com.lulobank.clients.services.ports.out.ParameterService;
import com.lulobank.parameters.sdk.dto.parameters.ParameterResponse;
import com.lulobank.parameters.sdk.operations.ParametersOperations;
import io.vavr.control.Try;

import java.util.Map;

public class ParameterServiceAdapter implements ParameterService {

    public static final String PROBLEMS_GETTING_PARAMETER = "Problems getting parameter %s for client %s";

    private final ParametersOperations retrofitParametersOperations;

    public ParameterServiceAdapter(ParametersOperations retrofitParametersOperations) {
        this.retrofitParametersOperations = retrofitParametersOperations;
    }

    @Override
    public Try<ParameterResponse> getParameterByKey(Map<String, String> headers, String clientId, String key) {
        return Try.of(() -> retrofitParametersOperations.getParameterByKey(headers, clientId, key))
                .onFailure(e -> {
                    throw new ParametersServiceException(String.format(PROBLEMS_GETTING_PARAMETER, key, clientId), e);
                });
    }

}
