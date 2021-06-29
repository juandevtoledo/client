package com.lulobank.clients.starter.v3.handler;

import com.lulobank.clients.services.application.port.in.UpdateProductOffersPort;
import com.lulobank.clients.v3.error.ClientsDataError;
import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.services.domain.productoffers.UpdateProductOffersRequest;
import com.lulobank.clients.services.domain.productoffers.UpdateStatus;
import com.lulobank.clients.starter.adapter.in.dto.GenericResponse;
import com.lulobank.clients.starter.adapter.in.mapper.GenericResponseMapper;
import com.lulobank.clients.starter.adapter.in.util.AdapterResponseUtil;
import com.lulobank.clients.v3.vo.AdapterCredentials;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import static com.lulobank.clients.starter.adapter.in.mapper.InboundAdapterErrorMapper.getHttpStatusFromBusinessCode;

public class ProductOffersHandler {

    private final UpdateProductOffersPort updateProductOffersPort;
    
    public ProductOffersHandler(UpdateProductOffersPort updateProductOffersPort) {
    	this.updateProductOffersPort = updateProductOffersPort;
    }
    
    public ResponseEntity<GenericResponse> updateClientProductOffers(HttpHeaders headers, String idClient,
                                                                     UpdateProductOffersRequest request) {
        request.setIdClient(idClient);
        request.setAdapterCredentials(new AdapterCredentials(headers.toSingleValueMap()));
        return updateProductOffersPort.execute(request)
                .filterOrElse(UpdateStatus::isSuccess, error -> ClientsDataError.connectionFailure())
                .fold(this::mapError, status -> ResponseEntity.accepted().build());
    }

    private ResponseEntity<GenericResponse> mapError(UseCaseResponseError useCaseResponseError) {
        return AdapterResponseUtil.error(GenericResponseMapper.INSTANCE.toErrorResponse(useCaseResponseError),
                getHttpStatusFromBusinessCode(useCaseResponseError.getBusinessCode()));
    }
}
