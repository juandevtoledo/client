package com.lulobank.clients.starter.v3.handler;

import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.starter.adapter.in.dto.ErrorResponse;
import com.lulobank.clients.starter.adapter.in.dto.GenericResponse;
import com.lulobank.clients.starter.adapter.in.util.AdapterResponseUtil;
import com.lulobank.clients.starter.v3.adapters.in.dto.UpdateEmailAddressRequest;
import com.lulobank.clients.v3.usecase.UpdateEmailAddressUseCase;
import com.lulobank.clients.v3.usecase.command.UpdateEmailAddress;
import org.springframework.http.ResponseEntity;

import static com.lulobank.clients.starter.adapter.in.mapper.InboundAdapterErrorMapper.getHttpStatusFromBusinessCode;

public class UpdateEmailAddressHandler {

    private final UpdateEmailAddressUseCase updateEmailAddressUseCase;

    public UpdateEmailAddressHandler(UpdateEmailAddressUseCase updateEmailAddressUseCase) {
        this.updateEmailAddressUseCase = updateEmailAddressUseCase;
    }

    public ResponseEntity<GenericResponse> updateEmailAddress(String idClient, UpdateEmailAddressRequest updateEmailAddressRequest) {
        return updateEmailAddressUseCase.execute(buildUpdateEmailAddress(idClient, updateEmailAddressRequest.getNewEmail()))
                .fold(this::mapError, response -> mapResponse());
    }

    private UpdateEmailAddress buildUpdateEmailAddress(String idClient, String emailAddress) {
        return UpdateEmailAddress.builder()
                .idClient(idClient)
                .newEmail(emailAddress)
                .build();
    }

    private ResponseEntity<GenericResponse> mapResponse() {
        return AdapterResponseUtil.ok();
    }

    private ResponseEntity<GenericResponse> mapError(UseCaseResponseError useCaseResponseError) {
        return new ResponseEntity<>(
                new ErrorResponse(useCaseResponseError.getProviderCode(),
                        useCaseResponseError.getBusinessCode(), useCaseResponseError.getDetail()),
                getHttpStatusFromBusinessCode(useCaseResponseError.getBusinessCode()));
    }
}
