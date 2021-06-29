package com.lulobank.clients.v3.usecase.riskEngineResultEventv2;

import com.lulobank.clients.v3.adapters.port.in.CreatePreApprovedOffer.CreatePreApprovedOfferPort;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.OfferState;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.RiskOfferV3;
import io.vavr.control.Try;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;


@CustomLog
@RequiredArgsConstructor
public class CreatePreApprovedOfferUseCase implements CreatePreApprovedOfferPort {

    private final ClientsV3Repository clientsV3Repository;

    private static final String PRE_APPROVED_TYPE = "CONFIRM_PREAPPROVED";
    private Integer MAX_TOTAL_AMOUNT_FROM_MOTOR;

    @Override
    public Try<Void> execute(ClientsV3Entity command) {
        MAX_TOTAL_AMOUNT_FROM_MOTOR = command.getValue();
        return clientsV3Repository.findByIdClient(command.getIdClient())
                .toTry()
                .peek(clientsV3Entity -> log.info("Updating  Pre approved offer to client with idClient {}", command.getIdClient()))
                .flatMap(clientsV3Entity -> processSave(clientsV3Entity))
                .flatMap(credit -> Try.run(() -> log.info(String.format("Pre Approved Offer has been saved ond BD"))));
    }

    private Try<ClientsV3Entity> processSave(ClientsV3Entity clientsV3Entity) {
        clientsV3Entity.getApprovedRiskAnalysis().getResults().add(buildRiskOfferV3());
        return clientsV3Repository.save(clientsV3Entity)
                .map(r -> clientsV3Entity);
    }

    private RiskOfferV3 buildRiskOfferV3() {
        return RiskOfferV3.builder()
                .idProductOffer(java.util.UUID.randomUUID().toString())
                .state(OfferState.ACTIVE)
                .type(PRE_APPROVED_TYPE)
                .value(MAX_TOTAL_AMOUNT_FROM_MOTOR)
                .offerDate(java.time.LocalDateTime.now())
                .build();
    }
}