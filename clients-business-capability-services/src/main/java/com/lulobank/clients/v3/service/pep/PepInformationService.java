package com.lulobank.clients.v3.service.pep;

import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.usecase.command.UpdatePepRequest;
import com.lulobank.clients.v3.usecase.pep.PepStatus;
import io.vavr.API;
import lombok.CustomLog;

import java.time.LocalDateTime;

import static io.vavr.API.$;
import static io.vavr.API.Case;

@CustomLog
public class PepInformationService {


    public static ClientsV3Entity setPepInformation(ClientsV3Entity clientsV3Entity, UpdatePepRequest updatePepRequest) {
        clientsV3Entity.setPep(getPepStatus(updatePepRequest.isPep()));
        clientsV3Entity.setDateResponsePep(LocalDateTime.now());
        return clientsV3Entity;
    }

    private static String getPepStatus(boolean pep) {
        return API.Match(pep).of(
                Case($(false), PepStatus.NO_PEP::value),
                Case($(true), PepStatus.PEP_WAIT_LIST::value)
        );
    }
}
