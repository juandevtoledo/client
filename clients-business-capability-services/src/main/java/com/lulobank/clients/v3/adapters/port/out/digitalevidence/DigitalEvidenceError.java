package com.lulobank.clients.v3.adapters.port.out.digitalevidence;

import com.lulobank.clients.services.application.util.HttpDomainStatus;
import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.v3.error.ClientsDataErrorStatus;

import static com.lulobank.clients.services.application.util.HttpDomainStatus.INTERNAL_SERVER_ERROR;
import static com.lulobank.clients.v3.error.ClientsDataErrorStatus.CLI_107;

public class DigitalEvidenceError extends UseCaseResponseError {


    public DigitalEvidenceError(String businessCode, HttpDomainStatus httpDomainStatus, String detail) {
        super(businessCode, httpDomainStatus, detail);
    }

    public static DigitalEvidenceError unknownError(){
        return new DigitalEvidenceError(CLI_107.name(), INTERNAL_SERVER_ERROR, ClientsDataErrorStatus.DEFAULT_DETAIL);
    }

}
