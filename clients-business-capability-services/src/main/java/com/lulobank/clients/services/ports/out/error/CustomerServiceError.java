package com.lulobank.clients.services.ports.out.error;

import com.lulobank.clients.services.application.util.HttpDomainStatus;
import com.lulobank.clients.services.domain.error.UseCaseResponseError;

import static com.lulobank.clients.services.ports.out.error.CustomerServiceErrorStatus.CLI_120;

public class CustomerServiceError extends UseCaseResponseError {

    public CustomerServiceError(CustomerServiceErrorStatus customerServiceErrorStatus, String providerCode) {
        super(customerServiceErrorStatus.name(), providerCode, CustomerServiceErrorStatus.DEFAULT_DETAIL);
    }

    public static CustomerServiceError connectionError() {
        return new CustomerServiceError(CLI_120, String.valueOf(HttpDomainStatus.INTERNAL_SERVER_ERROR.value()));
    }
}
