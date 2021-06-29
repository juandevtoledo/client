package com.lulobank.clients.sdk.operations;

import io.vavr.control.Try;

import java.util.Map;

public interface CreateCustomerOperation {

    Try<Boolean> createCustomer(Map<String, String> headers, String idClient);

}
