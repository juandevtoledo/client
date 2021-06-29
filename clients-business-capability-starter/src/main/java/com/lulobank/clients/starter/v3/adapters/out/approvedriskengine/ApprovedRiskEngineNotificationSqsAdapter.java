package com.lulobank.clients.starter.v3.adapters.out.approvedriskengine;

import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

import com.lulobank.clients.v3.adapters.port.out.approved.ApprovedRiskEngineNotification;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;

import io.vavr.control.Option;

public class ApprovedRiskEngineNotificationSqsAdapter implements ApprovedRiskEngineNotification {

	private final QueueMessagingTemplate queueMessagingTemplate;
	private final ApprovedRiskEngineEvent approvedRiskEngineEvent;

	public ApprovedRiskEngineNotificationSqsAdapter(QueueMessagingTemplate queueMessagingTemplate,
			ApprovedRiskEngineEvent approvedRiskEngineEvent) {
		this.queueMessagingTemplate = queueMessagingTemplate;
		this.approvedRiskEngineEvent = approvedRiskEngineEvent;
	}

	@Override
	public void notifyRiskEngineResponse(ClientsV3Entity clientsV3Entity) {
		Option.of(clientsV3Entity)
			.peek(e -> approvedRiskEngineEvent.send(e,
				r -> queueMessagingTemplate.convertAndSend(r.getEndpoint(), r.getEvent())));
	}

}
