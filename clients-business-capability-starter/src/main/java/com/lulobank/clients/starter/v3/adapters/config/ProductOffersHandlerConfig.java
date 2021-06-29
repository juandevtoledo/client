package com.lulobank.clients.starter.v3.adapters.config;

import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.lulobank.clients.services.application.port.in.UpdateProductOffersPort;
import com.lulobank.clients.starter.v3.adapters.out.savingsaccountservice.SavingAccountServiceAdapter;
import com.lulobank.clients.starter.v3.handler.ProductOfferHandler;
import com.lulobank.clients.starter.v3.handler.ProductOffersHandler;
import com.lulobank.clients.starter.v3.util.ProductOfferValidator;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.saving.SavingAccountService;
import com.lulobank.clients.v3.service.productoffers.ProductOfferValidatorService;
import com.lulobank.clients.v3.service.productoffers.SavingValidatorService;
import com.lulobank.clients.v3.service.productoffers.PepValidatorService;
import com.lulobank.clients.v3.usecase.productoffers.GetClientProductOfferUseCase;

import co.com.lulobank.tracing.restTemplate.RestTemplateClient;

@Configuration
@Import({RestTemplateClientConfig.class, ProductOfferTypeConfig.class})
public class ProductOffersHandlerConfig {
	
	@Bean
	public ProductOffersHandler productOffersHandler(UpdateProductOffersPort updateProductOffersPort) {
		return new ProductOffersHandler(updateProductOffersPort);
	}
	
	@Bean
	public ProductOfferHandler getProductOfferHandler(GetClientProductOfferUseCase getClientProductOfferUseCase) {
		return new ProductOfferHandler(getClientProductOfferUseCase);
	}

	@Bean
	public GetClientProductOfferUseCase getGetClientProductOfferUseCase(ClientsV3Repository clientsV3Repository,
			Map<String, ProductOfferValidatorService> productOfferValidators, ProductOfferTypeConfig productOfferTypeConfig) {
		return new GetClientProductOfferUseCase(clientsV3Repository, productOfferValidators, 
				productOfferTypeConfig.getValidators(), productOfferTypeConfig.getExpiredDays(), 
				productOfferTypeConfig.getDescriptions(), productOfferTypeConfig.getAdditionalInfo());
	}
	
	@Bean
	public SavingAccountService getSavingAccountServiceAdapter(@Qualifier("savingsRestTemplate")  RestTemplateClient savingsRestTemplateClient) {
		return new SavingAccountServiceAdapter(savingsRestTemplateClient);
	}
	
	@ProductOfferValidator(name = "pepValidator")
	public ProductOfferValidatorService getPepValidatorService() {
		return new PepValidatorService();
	}
	
	@ProductOfferValidator(name = "savingValidator")
	public ProductOfferValidatorService getSavingValidatorService(SavingAccountService savingAccountService) {
		return new SavingValidatorService(savingAccountService);
	}
}
