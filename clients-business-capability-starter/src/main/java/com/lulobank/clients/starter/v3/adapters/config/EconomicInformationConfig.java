package com.lulobank.clients.starter.v3.adapters.config;

import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.events.RiskEngineNotificationService;
import com.lulobank.clients.v3.service.economicinformation.EconomicInformationService;
import com.lulobank.clients.v3.usecase.economicinformation.SaveEconomicInformationInOnboardingUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lulobank.clients.services.ports.out.ParameterService;
import com.lulobank.clients.v3.usecase.economicinformation.SaveEconomicInformationUseCase;

@Configuration
public class EconomicInformationConfig {

	@Bean
	public SaveEconomicInformationUseCase getSaveEconomicInformationUseCase(ClientsV3Repository clientsRepository,
																			EconomicInformationService economicInformationService,
																			RiskEngineNotificationService riskEngineNotificationService) {
		return new SaveEconomicInformationUseCase(clientsRepository, economicInformationService, riskEngineNotificationService);
	}

	@Bean
	public SaveEconomicInformationInOnboardingUseCase getSaveEconomicInformationInOnboardingUseCase(ClientsV3Repository clientsRepository,
																									EconomicInformationService economicInformationService,
																									RiskEngineNotificationService riskEngineNotificationService) {

		return new SaveEconomicInformationInOnboardingUseCase(clientsRepository, economicInformationService, riskEngineNotificationService);
	}

	@Bean
	public EconomicInformationService getBuildEconomicInformationService(ClientsV3Repository clientsRepository,
                                                                         ParameterService parameterService){
		return new EconomicInformationService(clientsRepository,parameterService);
	}
}
