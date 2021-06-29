package com.lulobank.clients.services.ports.out;

import com.lulobank.clients.services.ports.out.dto.CustomerServiceResponse;
import com.lulobank.clients.services.ports.out.error.CustomerServiceError;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import io.vavr.control.Either;

import java.util.Map;

public interface CustomerServiceV2 {
    Either<CustomerServiceError, CustomerServiceResponse> createUserCustomer(Map<String, String> headers, ClientsV3Entity client);
}
