package com.lulobank.clients.starter.v3.adapters.out.credits.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.lulobank.clients.starter.v3.adapters.config.RestTemplateClientConfig;
import com.lulobank.clients.starter.v3.adapters.out.credits.CreditsServiceAdapter;
import com.lulobank.clients.v3.adapters.port.out.credits.CreditsService;

import co.com.lulobank.tracing.restTemplate.RestTemplateClient;


@Configuration
@Import({RestTemplateClientConfig.class})
public class CreditsServiceAdapterConfig {
	
	@Bean
	public CreditsService getCreditsService(@Qualifier("creditsRestTemplate")RestTemplateClient creditsRestTemplateClient) {
		return new CreditsServiceAdapter(creditsRestTemplateClient);
	}


}
