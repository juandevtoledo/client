package com.lulobank.clients.starter.v3.adapters.out.approvedriskengine;

import com.lulobank.clients.services.events.EventMapperV2;
import com.lulobank.clients.services.events.EventV2;
import com.lulobank.clients.starter.outboundadapter.sqs.SqsIntegration;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.CustomLog;

@CustomLog
public class ApprovedRiskEngineEvent extends SqsIntegration<ClientsV3Entity, RiskEngineResponseMessage> {

	public ApprovedRiskEngineEvent(String endpoint) {
		super(endpoint);
	}

	@Override
	public EventV2<RiskEngineResponseMessage> map(ClientsV3Entity clientsV3Entity) {
		return Try.of(() -> clientsV3Entity.getApprovedRiskAnalysis())
				.flatMap(risk -> Option.ofOptional(risk.getResults().stream().findFirst()).toTry())
				.map(riskOfferV3 -> ApprovedRiskEngineMapper.INSTANCE
						.riskOfferV3toRiskEngineResponseMessage(clientsV3Entity, riskOfferV3))
				.map(riskEngineResponseMessage -> EventMapperV2.of(riskEngineResponseMessage))
				.onFailure(ex -> log
						.error(String.format("Error mapping ClientsV3Entity to RiskEngineResponseMessage. IdClient: %s",
								clientsV3Entity.getIdClient()), ex))
				.get();
	}
}
