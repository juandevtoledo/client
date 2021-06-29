package com.lulobank.clients.starter.v3.handler;

import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.starter.adapter.in.dto.GenericResponse;
import com.lulobank.clients.starter.adapter.in.util.AdapterResponseUtil;
import com.lulobank.clients.starter.v3.mapper.ClientFatcaInformationMapper;
import com.lulobank.clients.v3.usecase.command.ValidateEmailIsUnique;
import com.lulobank.clients.v3.usecase.validations.EmailValidationsUseCase;
import com.lulobank.clients.v3.vo.AdapterCredentials;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static com.lulobank.clients.starter.adapter.in.mapper.InboundAdapterErrorMapper.getHttpStatusFromBusinessCode;

public class ClientValidationsHandler {

    private final EmailValidationsUseCase emailValidationsUseCase;

    public ClientValidationsHandler(EmailValidationsUseCase emailValidationsUseCase) {
        this.emailValidationsUseCase = emailValidationsUseCase;
    }

    public ResponseEntity<GenericResponse> validateEmail(final String email,final Map<String,String> header){
        return emailValidationsUseCase.execute(buildCommand(email, header))
                .fold(this::mapError, r-> AdapterResponseUtil.ok());
    }

    private ResponseEntity<GenericResponse> mapError(UseCaseResponseError useCaseResponseError) {
        return AdapterResponseUtil.error(ClientFatcaInformationMapper.INSTANCE.toErrorResponse(useCaseResponseError),
                getHttpStatusFromBusinessCode(useCaseResponseError.getBusinessCode()));
    }

    private ValidateEmailIsUnique buildCommand(String email, Map<String, String> header) {
        return ValidateEmailIsUnique.builder().email(email).credentials(new AdapterCredentials(header)).build();
    }
}
