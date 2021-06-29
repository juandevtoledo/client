package com.lulobank.clients.starter.v3.handler.fatca;

import com.lulobank.clients.starter.adapter.in.dto.GenericResponse;
import com.lulobank.clients.starter.adapter.in.util.AdapterResponseUtil;
import com.lulobank.clients.starter.v3.adapters.in.dto.ClientFatcaRequest;
import com.lulobank.clients.starter.v3.handler.fatca.util.ResponseManagementFatca;
import com.lulobank.clients.starter.v3.mapper.ClientFatcaInformationMapper;
import com.lulobank.clients.v3.usecase.fatca.ClientFatcaUseCase;
import com.lulobank.clients.v3.usecase.GetClientFatcaUseCase;
import com.lulobank.clients.v3.usecase.command.ClientFatcaInformation;
import com.lulobank.clients.v3.usecase.command.GetClientFatcaResponse;
import lombok.AllArgsConstructor;
import lombok.CustomLog;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@CustomLog
@AllArgsConstructor
@Component
public class ClientFatcaHandler {

    private final ClientFatcaUseCase clientFatcaUseCase;
    private final GetClientFatcaUseCase getClientFatcaUseCase;

    public ResponseEntity<GenericResponse> saveInformationFatca(ClientFatcaRequest request, String idClient) {
        return clientFatcaUseCase.execute(buildRequest(request, idClient))
                .fold(ResponseManagementFatca::mapError, ResponseManagementFatca::mapResponse);
    }

    public ResponseEntity<GenericResponse> getInformationFatca(String idClient) {
        return getClientFatcaUseCase.execute(idClient)
                .fold(ResponseManagementFatca::mapError, mapResponse());
    }

    private ClientFatcaInformation buildRequest(ClientFatcaRequest request, String idClient) {
        request.setIdClient(idClient);
        return ClientFatcaInformationMapper.INSTANCE.fromClientFatcaRequest(request);
    }

    private Function<GetClientFatcaResponse, ResponseEntity<GenericResponse>> mapResponse() {
        return res -> AdapterResponseUtil.ok(ClientFatcaInformationMapper.INSTANCE.fromGetClientFatcaResponse(res));
    }
}
