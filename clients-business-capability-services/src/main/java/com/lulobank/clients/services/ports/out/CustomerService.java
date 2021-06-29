package com.lulobank.clients.services.ports.out;

import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import io.vavr.control.Either;
import io.vavr.control.Try;

import java.util.Map;

public interface CustomerService {

  Try<Boolean> createUserCustomer(Map<String, String> headers, ClientsV3Entity client);

  Either<UseCaseResponseError,Boolean> isEmailExist(Map<String, String> headers, String email);

}
