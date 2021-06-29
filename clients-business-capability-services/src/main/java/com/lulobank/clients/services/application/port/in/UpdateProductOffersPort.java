package com.lulobank.clients.services.application.port.in;

import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.services.domain.productoffers.UpdateProductOffersRequest;
import com.lulobank.clients.services.domain.productoffers.UpdateStatus;
import io.vavr.control.Either;

public interface UpdateProductOffersPort extends UseCase<UpdateProductOffersRequest, Either<UseCaseResponseError, UpdateStatus>> {
}
