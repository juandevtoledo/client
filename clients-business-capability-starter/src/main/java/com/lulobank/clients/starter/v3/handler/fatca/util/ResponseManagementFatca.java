package com.lulobank.clients.starter.v3.handler.fatca.util;

import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.starter.adapter.in.dto.ErrorResponse;
import com.lulobank.clients.starter.adapter.in.dto.GenericResponse;
import com.lulobank.clients.starter.adapter.in.util.AdapterResponseUtil;
import com.lulobank.clients.starter.v3.adapters.in.dto.ClientFatcaRequest;
import com.lulobank.clients.starter.v3.mapper.ClientFatcaInformationMapper;
import com.lulobank.clients.v3.usecase.command.ClientFatcaInformation;
import com.lulobank.clients.v3.usecase.command.ClientFatcaResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.lulobank.clients.starter.adapter.in.mapper.InboundAdapterErrorMapper.getHttpStatusFromBusinessCode;
import static com.lulobank.clients.v3.error.ClientsDataError.internalError;

public class ResponseManagementFatca {

    public static ClientFatcaInformation buildRequest(ClientFatcaRequest request, String idClient) {
        request.setIdClient(idClient);
        return ClientFatcaInformationMapper.INSTANCE.fromClientFatcaRequest(request);
    }

    public static ResponseEntity<GenericResponse> mapResponse(ClientFatcaResponse response) {
        return response.isSuccess() ? AdapterResponseUtil.ok() : buildUnknownError();
    }

    private static ResponseEntity<GenericResponse> buildUnknownError() {
        ErrorResponse errorResponse = ClientFatcaInformationMapper.INSTANCE.toErrorResponse(internalError());
        return AdapterResponseUtil.error(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static ResponseEntity<GenericResponse> mapError(UseCaseResponseError useCaseResponseError) {
        return AdapterResponseUtil.error(ClientFatcaInformationMapper.INSTANCE.toErrorResponse(useCaseResponseError),
                getHttpStatusFromBusinessCode(useCaseResponseError.getBusinessCode()));
    }
}
