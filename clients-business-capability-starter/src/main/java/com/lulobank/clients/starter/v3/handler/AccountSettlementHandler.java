package com.lulobank.clients.starter.v3.handler;

import com.lulobank.clients.sdk.operations.dto.onboardingclients.AccountSettlement;
import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.starter.adapter.in.dto.GenericResponse;
import com.lulobank.clients.starter.adapter.in.util.AdapterResponseUtil;
import com.lulobank.clients.starter.v3.mapper.AccountSettlementMapper;
import com.lulobank.clients.starter.v3.mapper.ClientFatcaInformationMapper;
import com.lulobank.clients.v3.usecase.accountsettlement.AccountSettlementUseCase;
import com.lulobank.clients.v3.usecase.command.BiometricResponse;
import lombok.CustomLog;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static com.lulobank.clients.starter.adapter.in.mapper.InboundAdapterErrorMapper.getHttpStatusFromBusinessCode;

@Component
@CustomLog
public class AccountSettlementHandler {

    private final AccountSettlementUseCase accountSettlementUseCase;

    public AccountSettlementHandler(AccountSettlementUseCase accountSettlementUseCase){
        this.accountSettlementUseCase = accountSettlementUseCase;
    }

    public ResponseEntity<GenericResponse> processAccountSettlement(String idClient){
        return accountSettlementUseCase.execute(setClient(idClient))
                .fold(AccountSettlementHandler::mapError,
                        AccountSettlementHandler::mapResponse);
    }

    public static ResponseEntity<GenericResponse> mapError(UseCaseResponseError useCaseResponseError) {
        return AdapterResponseUtil.error(ClientFatcaInformationMapper.INSTANCE.toErrorResponse(useCaseResponseError),
                getHttpStatusFromBusinessCode(useCaseResponseError.getBusinessCode()));
    }

    public static ResponseEntity<GenericResponse> mapResponse(BiometricResponse response) {
        return AdapterResponseUtil.accepted(AccountSettlementMapper.INSTANCE.accountSettlementResponseTo(response));
    }

    private AccountSettlement setClient(String idClient){
        return AccountSettlement.builder().idClient(idClient).build();
    }

}
