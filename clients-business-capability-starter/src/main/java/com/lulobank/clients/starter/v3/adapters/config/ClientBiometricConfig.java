package com.lulobank.clients.starter.v3.adapters.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lulobank.clients.services.actions.MessageToSQSUpdateIdBiometricIdentity;
import com.lulobank.clients.services.features.RetriesOption;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.firebase.UserStateRepository;
import com.lulobank.clients.v3.usecase.ClientsBiometricUseCase;

@Configuration
public class ClientBiometricConfig {

	@Value("${cloud.aws.sqs.client-events}")
	private String sqsClientEventsEndPoint;

	@Bean
	public MessageToSQSUpdateIdBiometricIdentity getMessageToSQSUpdateIdBiometricIdentity(
			QueueMessagingTemplate queueMessagingTemplate, RetriesOption adotechResponseRetriesOption) {
		return new MessageToSQSUpdateIdBiometricIdentity(queueMessagingTemplate, sqsClientEventsEndPoint,
				adotechResponseRetriesOption);
	}

	@Bean
	public ClientsBiometricUseCase getClientsBiometricUseCase(ClientsV3Repository clientsV3Repository,
			UserStateRepository userStateFirebaseRepository, MessageToSQSUpdateIdBiometricIdentity sqsUpdateIdBiometricIdentity) {
		return new ClientsBiometricUseCase(clientsV3Repository, userStateFirebaseRepository, sqsUpdateIdBiometricIdentity);
	}
}
