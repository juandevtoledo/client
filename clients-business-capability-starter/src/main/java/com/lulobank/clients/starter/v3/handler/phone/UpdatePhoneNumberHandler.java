package com.lulobank.clients.starter.v3.handler.phone;

import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.starter.adapter.in.dto.ErrorResponse;
import com.lulobank.clients.starter.adapter.in.dto.GenericResponse;
import com.lulobank.clients.starter.adapter.in.mapper.GenericResponseMapper;
import com.lulobank.clients.starter.adapter.in.util.AdapterResponseUtil;
import com.lulobank.clients.starter.v3.adapters.in.phone.dto.UpdatePhoneRequest;
import com.lulobank.clients.v3.usecase.command.UpdatePhoneNumber;
import com.lulobank.clients.v3.usecase.phone.UpdatePhoneNumberUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static com.lulobank.clients.starter.adapter.in.mapper.InboundAdapterErrorMapper.getHttpStatusFromBusinessCode;
import static com.lulobank.clients.v3.error.ClientsDataError.internalError;

@Component
public class UpdatePhoneNumberHandler {

    private final UpdatePhoneNumberUseCase updatePhoneNumberUseCase;

    public UpdatePhoneNumberHandler(UpdatePhoneNumberUseCase updatePhoneNumberUseCase) {
        this.updatePhoneNumberUseCase = updatePhoneNumberUseCase;
    }

    public ResponseEntity<GenericResponse> updatePhone(String idClient, UpdatePhoneRequest updatePhoneRequest) {
        return updatePhoneNumberUseCase.execute(toUpdatePhoneNumber(idClient, updatePhoneRequest))
        .fold(this::mapError, this::mapResponse);
    }

    private UpdatePhoneNumber toUpdatePhoneNumber(String idClient, UpdatePhoneRequest updatePhoneRequest) {
        return UpdatePhoneNumber.builder()
                .idClient(idClient)
                .newPhoneNumber(updatePhoneRequest.getNewPhoneNumber())
                .countryCode(updatePhoneRequest.getCountryCode())
                .build();
    }

    private ResponseEntity<GenericResponse> mapResponse(boolean response) {
        return response ? AdapterResponseUtil.ok() : buildUnknownError();
    }

    private ResponseEntity<GenericResponse> buildUnknownError() {
        ErrorResponse errorResponse = GenericResponseMapper.INSTANCE.toErrorResponse(internalError());
        return AdapterResponseUtil.error(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<GenericResponse> mapError(UseCaseResponseError useCaseResponseError) {
        return AdapterResponseUtil.error(GenericResponseMapper.INSTANCE.toErrorResponse(useCaseResponseError),
                getHttpStatusFromBusinessCode(useCaseResponseError.getBusinessCode()));
    }
}
