package com.lulobank.clients.starter.v3.adapters.out.approvedriskengine;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lulobank.clients.v3.adapters.port.out.approved.ApprovedRiskEngineNotification;

@Configuration
public class ApprovedRiskEngineNotificationConfig {

	@Value("${cloud.aws.sqs.credits-events}")
	private String sqsCreditsEventsEndPoint;

	@Bean
	public ApprovedRiskEngineEvent getApprovedRiskEngineEvent() {
		return new ApprovedRiskEngineEvent(sqsCreditsEventsEndPoint);
	}

	@Bean
	public ApprovedRiskEngineNotification getApprovedRiskEngineNotification(
			ApprovedRiskEngineEvent approvedRiskEngineEvent, QueueMessagingTemplate queueMessagingTemplate) {
		return new ApprovedRiskEngineNotificationSqsAdapter(queueMessagingTemplate, approvedRiskEngineEvent);
	}

}
