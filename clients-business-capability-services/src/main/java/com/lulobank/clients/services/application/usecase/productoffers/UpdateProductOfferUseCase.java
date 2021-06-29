package com.lulobank.clients.services.application.usecase.productoffers;

import com.lulobank.clients.services.application.port.in.UpdateProductOffersPort;
import com.lulobank.clients.v3.error.ClientsDataError;
import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.services.domain.productoffers.OfferStatus;
import com.lulobank.clients.services.domain.productoffers.UpdateProductOffersRequest;
import com.lulobank.clients.services.domain.productoffers.UpdateStatus;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.OfferState;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.RiskOfferV3;
import io.vavr.collection.Stream;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;

@Slf4j
@RequiredArgsConstructor
public class UpdateProductOfferUseCase implements UpdateProductOffersPort {

    private final ClientsV3Repository clientsV3Repository;

    @Override
    public Either<UseCaseResponseError, UpdateStatus> execute(UpdateProductOffersRequest command) {
        return clientsV3Repository.findByIdClient(command.getIdClient())
                .toEither(ClientsDataError.clientNotFound())
                .filterOrElse(this::isValidOffer, error -> ClientsDataError.clientNotFound())
                .flatMap(entity -> updateOffer(entity, command))
                .map(entity -> new UpdateStatus(true))
                .mapLeft(Function.identity());
    }

    private Either<ClientsDataError, ClientsV3Entity> updateOffer(ClientsV3Entity entity, UpdateProductOffersRequest request) {
        return findOffer(entity.getApprovedRiskAnalysis().getResults(), request.getIdProductOffer())
                .toEither(ClientsDataError.clientNotFound())
                .peekLeft(error -> log.error("Error finding the offer {} for client {}", request.getIdProductOffer(), request.getIdClient()))
                .map(mapWithState(request.getStatus()))
                .peek(offer -> clientsV3Repository.save(entity))
                .map(offer -> entity);
    }

    private UnaryOperator<RiskOfferV3> mapWithState(OfferStatus status) {
        return offer -> {
            offer.setState(OfferState.valueOf(status.name()));
            return offer;
        };
    }

    private boolean isValidOffer(ClientsV3Entity entity) {
        return Option.of(entity.getApprovedRiskAnalysis())
                .map(offers -> !offers.getResults().isEmpty())
                .getOrElse(false);
    }

    private Option<RiskOfferV3> findOffer(List<RiskOfferV3> offers, String idOffer) {
        return Stream.ofAll(offers).find(offer -> idOffer.equalsIgnoreCase(offer.getIdProductOffer()));
    }
}
