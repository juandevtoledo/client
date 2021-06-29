package com.lulobank.clients.starter.v3.handler.fatca;

import com.lulobank.clients.starter.adapter.in.dto.GenericResponse;
import com.lulobank.clients.starter.v3.adapters.in.dto.ClientFatcaRequest;
import com.lulobank.clients.starter.v3.handler.fatca.util.ResponseManagementFatca;
import com.lulobank.clients.starter.v3.mapper.ClientFatcaInformationMapper;
import com.lulobank.clients.v3.usecase.command.ClientFatcaInformation;
import com.lulobank.clients.v3.usecase.fatca.ClientFatcaOnboardingUseCase;
import lombok.AllArgsConstructor;
import lombok.CustomLog;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@CustomLog
@AllArgsConstructor
@Component
public class ClientFatcaOnboardingHandler {

    private final ClientFatcaOnboardingUseCase clientFatcaOnboardingUseCase;

    public ResponseEntity<GenericResponse> saveInformationFatca(ClientFatcaRequest request, String idClient) {
        return clientFatcaOnboardingUseCase.execute(buildRequest(request, idClient))
                .fold(ResponseManagementFatca::mapError, ResponseManagementFatca::mapResponse);
    }

    private static ClientFatcaInformation buildRequest(ClientFatcaRequest request, String idClient) {
        request.setIdClient(idClient);
        return ClientFatcaInformationMapper.INSTANCE.fromClientFatcaRequest(request);
    }


}
