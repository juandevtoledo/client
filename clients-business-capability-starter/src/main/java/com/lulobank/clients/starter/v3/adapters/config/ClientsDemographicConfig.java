package com.lulobank.clients.starter.v3.adapters.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.usecase.ClientsDemographicUseCase;

@Configuration
public class ClientsDemographicConfig {
	
	@Bean
	public ClientsDemographicUseCase getClientsDemographicUseCase(ClientsV3Repository clientsV3Repository) {
		return new ClientsDemographicUseCase(clientsV3Repository);
	}

}
