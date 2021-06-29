package com.lulobank.clients.v3.adapters.port.in.CreatePreApprovedOffer;

import com.lulobank.clients.services.application.port.in.UseCase;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import io.vavr.control.Try;

public interface CreatePreApprovedOfferPort extends UseCase<ClientsV3Entity, Try<Void>> {
}
