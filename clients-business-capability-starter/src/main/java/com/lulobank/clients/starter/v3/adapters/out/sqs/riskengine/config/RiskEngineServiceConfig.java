package com.lulobank.clients.starter.v3.adapters.out.sqs.riskengine.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lulobank.clients.starter.v3.adapters.out.sqs.riskengine.RiskEngineServiceSqsAdapter;
import com.lulobank.clients.v3.adapters.port.out.riskengine.RiskEngineService;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;

@Configuration
public class RiskEngineServiceConfig {
	
    @Value("${cloud.aws.sqs.risk-engine-events}")
    private String riskEngineSqsEndpoint;
	
	@Bean
	public RiskEngineService getRiskEngineService(SqsBraveTemplate sqsBraveTemplate) {
		return new RiskEngineServiceSqsAdapter(sqsBraveTemplate, riskEngineSqsEndpoint);
	}
}
