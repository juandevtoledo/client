package com.lulobank.clients.starter.outboundadapter.customerservice;

import co.com.lulobank.tracing.restTemplate.RestTemplateClient;
import com.lulobank.clients.services.ports.out.CustomerServiceV2;
import com.lulobank.clients.services.ports.out.dto.CustomerServiceResponse;
import com.lulobank.clients.services.ports.out.error.CustomerServiceError;
import com.lulobank.clients.starter.outboundadapter.customerservice.dto.CreateCustomerRequest;
import com.lulobank.clients.starter.outboundadapter.customerservice.mapper.CustomerServiceAdapterMapper;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import io.vavr.control.Either;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@CustomLog
@RequiredArgsConstructor
public class CustomerServiceAdapterV2 implements CustomerServiceV2 {

    private static final String ERROR_CUSTOMER_SERVICE_CREATION = "Error creating client in customer service. Error %s";
    public static final String BASE_PATH = "customer-service";
    public static final String CREATE_USER_CUSTOMER = "/api/v1/customers";

    private final RestTemplateClient customerRestTemplateClient;

    @Override
    public Either<CustomerServiceError, CustomerServiceResponse> createUserCustomer(Map<String, String> headers, ClientsV3Entity client) {
        return createCustomer(headers, CustomerServiceAdapterMapper.INSTANCE.toCreateCustomerRequest(client));
    }

    private Either<CustomerServiceError, CustomerServiceResponse> createCustomer(Map<String, String> headers, CreateCustomerRequest createCustomerRequest) {
        return customerRestTemplateClient.post(BASE_PATH.concat(CREATE_USER_CUSTOMER),createCustomerRequest,headers,Object.class)
                .peekLeft( error -> log.error(String.format(ERROR_CUSTOMER_SERVICE_CREATION , error.getBody())))
                .mapLeft( httpError -> CustomerServiceError.connectionError())
                .map(responseEntity -> responseEntity.getStatusCode().is2xxSuccessful())
                .map( value -> CustomerServiceResponse.builder().created(value).build());
    }
}
