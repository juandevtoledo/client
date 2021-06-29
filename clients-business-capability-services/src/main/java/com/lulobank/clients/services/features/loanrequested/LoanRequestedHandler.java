package com.lulobank.clients.services.features.loanrequested;

import com.amazonaws.SdkClientException;
import com.lulobank.clients.sdk.operations.dto.ClientLoanRequested;
import com.lulobank.clients.services.exception.ClientNotFoundException;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.utils.ClientErrorResultsEnum;
import com.lulobank.clients.services.utils.ProductTypeEnum;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.LoanClientRequestedV3;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.OnBoardingStatusV3;
import com.lulobank.clients.v3.mapper.ClientEntityMapper;
import com.lulobank.core.Handler;
import com.lulobank.core.Response;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.util.Objects;

import static com.lulobank.clients.services.utils.ProductTypeEnum.CREDIT_ACCOUNT;
import static com.lulobank.core.utils.ValidatorUtils.getListValidations;

@Slf4j
public class LoanRequestedHandler implements Handler<Response<ClientEntity>, ClientLoanRequested> {

    private final ClientsV3Repository clientsV3Repository;

    public LoanRequestedHandler(ClientsV3Repository clientsV3Repository) {
        this.clientsV3Repository = clientsV3Repository;
    }

    @Override
    public Response<ClientEntity> handle(ClientLoanRequested request) {
        return clientsV3Repository.findByIdClient(request.getIdClient())
                .toTry()
                .flatMap(entity -> updateLoanRequested(entity, request))
                .map(ClientEntityMapper.INSTANCE::fromEntityV3)
                .map(Response::new)
                .recover(SdkClientException.class, this::getErrorResponse)
                .getOrElseThrow(() -> new ClientNotFoundException(request.getIdClient()));
    }

    private Response<ClientEntity> getErrorResponse(Throwable e) {
        log.error("Dynamo error {}", e.getMessage());
        return new Response<>(getListValidations(ClientErrorResultsEnum.INTERNAL_SERVER_ERROR.name(),
                String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())));
    }

    private ClientsV3Entity getFieldToUpdate(ClientsV3Entity entity, LoanClientRequestedV3 loanClient) {
        return Option.of(entity.getLoanRequested())
                .filter(Objects::nonNull)
                .map(loanRequestedV3 -> caseLoanRequested(entity, loanClient))
                .getOrElse(() -> getFieldOnBoarding(entity, loanClient));
    }


    private ClientsV3Entity getFieldOnBoarding(ClientsV3Entity entity, LoanClientRequestedV3 loanClient) {
        return Option.of(entity.getOnBoardingStatus())
                .map(OnBoardingStatusV3::getProductSelected)
                .filter(Objects::nonNull)
                .filter(product -> CREDIT_ACCOUNT == ProductTypeEnum.valueOf(product))
                .map(field -> getClientsV3Entity(entity, loanClient))
                .getOrNull();
    }


    private Try<ClientsV3Entity> updateLoanRequested(ClientsV3Entity entity, ClientLoanRequested request) {
        LoanClientRequestedV3 loan = new LoanClientRequestedV3(request.getAmount(), request.getLoanPurpose());
        return Try.of(() -> getFieldToUpdate(entity, loan))
                .filter(Objects::nonNull)
                .peek(field -> log.info("Updating entity with loanRequested in field: {}", field.toString()))
                .flatMap(clientsV3Repository::save)
                .onFailure(e -> log.error("Error trying to determine field to update: {}", e.getMessage()));
    }


    private ClientsV3Entity getClientsV3Entity(ClientsV3Entity entity, LoanClientRequestedV3 loanClient) {
        entity.getOnBoardingStatus().setLoanClientRequested(loanClient);
        return entity;
    }

    private ClientsV3Entity caseLoanRequested(ClientsV3Entity entity, LoanClientRequestedV3 loanClient) {
        entity.getLoanRequested().setLoanClientRequested(loanClient);
        return entity;
    }

}
