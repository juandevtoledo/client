package com.lulobank.clients.starter.v3.handler;

import static com.google.common.collect.ImmutableList.of;
import static com.lulobank.clients.starter.adapter.in.mapper.InboundAdapterErrorMapper.getHttpStatusFromBusinessCode;

import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import com.lulobank.clients.starter.adapter.in.dto.ErrorResponse;
import com.lulobank.clients.starter.adapter.in.dto.GenericResponse;
import com.lulobank.clients.starter.adapter.in.util.AdapterResponseUtil;
import com.lulobank.clients.starter.v3.adapters.in.dto.ApprovedOffersResponse;
import com.lulobank.clients.starter.v3.adapters.in.dto.ApprovedOffersResponse.ApprovedOffer;
import com.lulobank.clients.v3.usecase.productoffers.GetClientProductOfferUseCase;
import com.lulobank.clients.v3.usecase.productoffers.command.ClientProductOffer;
import com.lulobank.clients.v3.usecase.productoffers.command.GetClientProductOfferRequest;

public class ProductOfferHandler {

	private final GetClientProductOfferUseCase getClientProductOfferUseCase;

	public ProductOfferHandler(GetClientProductOfferUseCase getClientProductOfferUseCase) {
		this.getClientProductOfferUseCase = getClientProductOfferUseCase;
	}

	public ResponseEntity<GenericResponse> getClientProductOffers(HttpHeaders headers, String idClient) {
		return getClientProductOfferUseCase.execute(buildGetClientProductOfferRequest(idClient, headers))
				.fold(this::mapError, this::mapResponse);
	}
	
	private ResponseEntity<GenericResponse> mapResponse(ClientProductOffer response) {
        return AdapterResponseUtil.ok(ApprovedOffersResponse.builder()
        		.offers(of(getApprovedOffer(response)))
        		.build());
    }
    
    private ApprovedOffer getApprovedOffer(ClientProductOffer response) {
    	return ApprovedOffer.builder()
    			.idProductOffer(response.getIdProductOffer())
    			.state(response.getState())
    			.type(response.getType())
    			.expiredDays(response.getExpiredDays())
    			.description(response.getDescription())
    			.additionalInfo(response.getAdditionalInfo())
    			.value(response.getValue())
    			.build();
    }
	
	private GetClientProductOfferRequest buildGetClientProductOfferRequest(String idClient, HttpHeaders headers) {
        return GetClientProductOfferRequest.builder()
        		.idClient(idClient)
        		.auth(headers.toSingleValueMap())
        		.build();
    }
    
    private ResponseEntity<GenericResponse> mapError(UseCaseResponseError useCaseResponseError) {
		return new ResponseEntity<>(
                new ErrorResponse(useCaseResponseError.getProviderCode(),
                        useCaseResponseError.getBusinessCode(), useCaseResponseError.getDetail()),
                getHttpStatusFromBusinessCode(useCaseResponseError.getBusinessCode()));
	}
}
