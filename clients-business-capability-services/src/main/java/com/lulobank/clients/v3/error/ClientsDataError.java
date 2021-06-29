package com.lulobank.clients.v3.error;

import com.lulobank.clients.services.application.util.HttpDomainStatus;
import com.lulobank.clients.services.domain.error.UseCaseResponseError;

import static com.lulobank.clients.v3.error.ClientsDataErrorStatus.CLI_100;
import static com.lulobank.clients.v3.error.ClientsDataErrorStatus.CLI_101;
import static com.lulobank.clients.v3.error.ClientsDataErrorStatus.CLI_103;
import static com.lulobank.clients.v3.error.ClientsDataErrorStatus.CLI_104;
import static com.lulobank.clients.v3.error.ClientsDataErrorStatus.CLI_105;
import static com.lulobank.clients.v3.error.ClientsDataErrorStatus.CLI_108;
import static com.lulobank.clients.v3.error.ClientsDataErrorStatus.CLI_109;

public class ClientsDataError extends UseCaseResponseError {

    public ClientsDataError(ClientsDataErrorStatus clientsDataErrorStatus, HttpDomainStatus providerCode) {
        super(clientsDataErrorStatus.name(), providerCode, ClientsDataErrorStatus.DEFAULT_DETAIL);
    }

    public ClientsDataError(ClientsDataErrorStatus clientsDataErrorStatus, HttpDomainStatus providerCode, String detail) {
        super(clientsDataErrorStatus.name(), providerCode, detail);
    }

    public static ClientsDataError connectionFailure() {
        return new ClientsDataError(CLI_100, HttpDomainStatus.BAD_GATEWAY);
    }

    public static ClientsDataError clientNotFound() {
        return new ClientsDataError(CLI_101, HttpDomainStatus.NOT_FOUND);
    }

    public static ClientsDataError fatcaInfoNotFound() {
        return new ClientsDataError(CLI_103, HttpDomainStatus.NOT_FOUND);
    }

    public static ClientsDataError internalError() {
        return new ClientsDataError(CLI_100, HttpDomainStatus.INTERNAL_SERVER_ERROR);
    }

    public static ClientsDataError internalServerError() {
        return new ClientsDataError(CLI_109, HttpDomainStatus.INTERNAL_SERVER_ERROR);
    }

    public static ClientsDataError emailIsNotUnique() {
        return new ClientsDataError(CLI_104, HttpDomainStatus.PRECONDITION_FAILED, ClientsDataErrorStatus.VALIDATION_DETAIL);
    }

    public static ClientsDataError emailIsNotUniqueInCustomerService() {
        return new ClientsDataError(CLI_105, HttpDomainStatus.CONFLICT, ClientsDataErrorStatus.VALIDATION_DETAIL);
    }

    public static ClientsDataError phoneIsNotUniqueInCustomerService() {
        return new ClientsDataError(CLI_108, HttpDomainStatus.CONFLICT, ClientsDataErrorStatus.VALIDATION_DETAIL);
    }
}
