package com.lulobank.clients.starter.outboundadapter.customerservice;

import co.com.lulobank.tracing.restTemplate.RestTemplateClient;
import com.lulobank.clients.services.ports.out.dto.CustomerServiceResponse;
import com.lulobank.clients.services.ports.out.error.CustomerServiceError;
import com.lulobank.clients.services.ports.out.error.CustomerServiceErrorStatus;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import io.vavr.control.Either;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static com.lulobank.clients.starter.v3.adapters.out.Sample.getClientsV3Entity;
import static com.lulobank.clients.starter.v3.adapters.out.Sample.getHeaders;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;
import co.com.lulobank.tracing.restTemplate.HttpError;

public class CustomerServiceAdapterV2Test {

    private CustomerServiceAdapterV2 customerServiceAdapterV2;

    @Mock
    private RestTemplateClient restTemplateClient;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        customerServiceAdapterV2 = new CustomerServiceAdapterV2(restTemplateClient);
    }

    @Test
    public void shouldResponse200AndCreatedTrue(){
        Map<String,String> headers  = getHeaders();
        ClientsV3Entity clientsV3Entity = getClientsV3Entity();
        when(restTemplateClient.post(any(),any(),any(),any()))
                .thenReturn(Either.right(ResponseEntity.accepted().build()));
        Either<CustomerServiceError, CustomerServiceResponse> result = customerServiceAdapterV2.createUserCustomer(headers,clientsV3Entity);
        assertTrue(result.isRight());
        assertTrue(result.get().getCreated());
        verify(restTemplateClient, times(1)).post(any(),any(),any(),any());
    }

    @Test
    public void shouldResponseErrorAndReturnCustomerServiceError(){
        Map<String,String> headers  = getHeaders();
        ClientsV3Entity clientsV3Entity = getClientsV3Entity();
        when(restTemplateClient.post(any(),any(),any(),any()))
                .thenReturn(Either.left(new HttpError("500", "Unxepected error trying to consume rest client", null)));
        Either<CustomerServiceError, CustomerServiceResponse> result = customerServiceAdapterV2.createUserCustomer(headers,clientsV3Entity);
        assertTrue(result.isLeft());
        assertEquals(CustomerServiceErrorStatus.DEFAULT_DETAIL,result.getLeft().getDetail());
        verify(restTemplateClient, times(1)).post(any(),any(),any(),any());
    }
}