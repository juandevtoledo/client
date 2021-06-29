package com.lulobank.clients.v3.usecase;

import com.lulobank.clients.services.application.port.in.UseCase;
import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.usecase.command.GetClientFatcaResponse;
import com.lulobank.clients.v3.usecase.mapper.FatcaInformationMapper;
import io.vavr.control.Either;
import lombok.CustomLog;

import java.util.function.Function;

import static com.lulobank.clients.v3.error.ClientsDataError.clientNotFound;
import static com.lulobank.clients.v3.error.ClientsDataError.fatcaInfoNotFound;
import static java.util.Objects.nonNull;

@CustomLog
public class GetClientFatcaUseCase implements UseCase<String, Either<UseCaseResponseError, GetClientFatcaResponse>> {

    private final ClientsV3Repository clientsV3Repository;

    public GetClientFatcaUseCase(ClientsV3Repository clientsV3Repository) {
        this.clientsV3Repository = clientsV3Repository;
    }

    @Override
    public Either<UseCaseResponseError, GetClientFatcaResponse> execute(String idClient) {
        return clientsV3Repository.findByIdClient(idClient)
                .toEither(clientNotFound())
                .filterOrElse(entity -> nonNull(entity.getFatcaInformation()), err -> fatcaInfoNotFound())
                .map(this::mapFatcaInfo)
                .mapLeft(Function.identity());
    }

    private GetClientFatcaResponse mapFatcaInfo(ClientsV3Entity entity) {
        return FatcaInformationMapper.INSTANCE.toGetClientFatcaResponse(entity);
    }
}
