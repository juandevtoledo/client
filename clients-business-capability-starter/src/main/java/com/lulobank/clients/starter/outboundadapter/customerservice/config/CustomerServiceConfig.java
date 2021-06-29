package com.lulobank.clients.starter.outboundadapter.customerservice.config;

import co.com.lulobank.tracing.restTemplate.RestTemplateClient;
import com.lulobank.clients.services.ports.out.CustomerService;
import com.lulobank.clients.services.ports.out.CustomerServiceV2;
import com.lulobank.clients.services.usecase.CreateCustomerUseCase;
import com.lulobank.clients.starter.outboundadapter.customerservice.CustomerServiceAdapter;
import com.lulobank.clients.starter.outboundadapter.customerservice.CustomerServiceAdapterV2;
import com.lulobank.clients.starter.outboundadapter.customerservice.CustomerServiceClient;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomerServiceConfig {

    @Value("${services.customer.url}")
    private String serviceDomain;

    @Bean
    public CustomerServiceClient customerServiceClient() {
        return new CustomerServiceClient(serviceDomain);
    }

    @Bean
    public CustomerService getCustomerService(CustomerServiceClient customerServiceClient) {
        return new CustomerServiceAdapter(customerServiceClient);
    }

    @Bean
    public CreateCustomerUseCase getCreateCustomerUseCase(CustomerService customerService,
                                                                        ClientsV3Repository clientsRepository
    ) {
        return new CreateCustomerUseCase(customerService, clientsRepository);
    }

    @Bean
    public CustomerServiceV2 getCustomerServiceAdapterV2(@Qualifier("customerRestTemplate") RestTemplateClient customerRestTemplate){
        return new CustomerServiceAdapterV2(customerRestTemplate);
    }
}
