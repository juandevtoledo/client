package com.lulobank.clients.starter.v3.adapters.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.com.lulobank.tracing.restTemplate.RestTemplateClient;

@Configuration
public class RestTemplateClientConfig {

	@Value("${services.savings.url}")
	private String savingsDomain;

	@Value("${services.customer.url}")
	private String customerDomain;

	@Value("${services.digital-evidence-xbc.url}")
	private String digitalEvidenceDomain;

	@Value("${services.profile-xbc.url}")
	private String profileDomain;
	
	@Value("${services.credits.url}")
	private String creditsDomain;

	@Value("${services.reporting.url}")
	private String reportingDomain;

	@Bean("savingsRestTemplate")
	public RestTemplateClient getSavingsRestTemplate(RestTemplateBuilder restTemplateBuilder) {
		return new RestTemplateClient(savingsDomain, restTemplateBuilder);
	}

	@Bean("customerRestTemplate")
	public RestTemplateClient getCustomerRestTemplate(RestTemplateBuilder restTemplateBuilder) {
		return new RestTemplateClient(customerDomain, restTemplateBuilder);
	}

	@Bean("digitalEvidenceRestTemplate")
	public RestTemplateClient getDigitalEvidenceRestTemplate(RestTemplateBuilder restTemplateBuilder){
		return new RestTemplateClient(digitalEvidenceDomain,restTemplateBuilder);
	}

	@Bean("profileRestTemplate")
	public RestTemplateClient getClientsRestTemplate(RestTemplateBuilder restTemplateBuilder) {
		return new RestTemplateClient(profileDomain, restTemplateBuilder);
	}
	
	@Bean("creditsRestTemplate")
	public RestTemplateClient getCreditsRestTemplate(RestTemplateBuilder restTemplateBuilder) {
		return new RestTemplateClient(creditsDomain, restTemplateBuilder);
	}

	@Bean("reportingRestTemplate")
	public RestTemplateClient getReportingRestTemplate(RestTemplateBuilder restTemplateBuilder) {
		return new RestTemplateClient(reportingDomain, restTemplateBuilder);
	}
}
