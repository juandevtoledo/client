package com.lulobank.clients.services.application.port.in;

import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.services.domain.productoffers.ClientProductOffersRequest;
import com.lulobank.clients.services.domain.productoffers.ClientProductOffer;
import io.vavr.control.Either;

public interface ClientProductOffersPort
        extends UseCase<ClientProductOffersRequest, Either<UseCaseResponseError, ClientProductOffer>> {
}
