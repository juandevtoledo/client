package com.lulobank.clients.starter.outboundadapter.parameters;

import com.lulobank.clients.services.ports.out.ParameterService;
import com.lulobank.parameters.sdk.operations.impl.RetrofitParametersOperations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ParameterServiceConfig {

    @Value("${services.parameters.url}")
    private String serviceDomain;

    @Bean
    public RetrofitParametersOperations getRetrofitParametersOperations() {
        return new RetrofitParametersOperations(serviceDomain);
    }

    @Bean
    public ParameterService getParameterService(RetrofitParametersOperations retrofitParametersOperations) {
        return new ParameterServiceAdapter(retrofitParametersOperations);
    }

}
