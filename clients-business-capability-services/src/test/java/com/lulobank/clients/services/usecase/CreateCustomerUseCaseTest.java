package com.lulobank.clients.services.usecase;

import com.lulobank.clients.services.Constants;
import com.lulobank.clients.services.exception.CreateCustomerException;
import com.lulobank.clients.services.exception.DigitalEvidenceException;
import com.lulobank.clients.services.ports.out.CustomerService;
import com.lulobank.clients.services.ports.out.DigitalEvidenceService;
import com.lulobank.clients.services.usecase.command.CreateCustomer;
import com.lulobank.clients.services.usecase.command.SaveDigitalEvidence;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CreateCustomerUseCaseTest {

    @Mock private ClientsV3Repository clientsV3Repository;
    @Mock private CustomerService customerService;

    @Captor private ArgumentCaptor<String> textCaptor;
    @Captor private ArgumentCaptor<ClientsV3Entity> clientEntityCaptor;

    @InjectMocks private CreateCustomerUseCase createCustomerUseCase;

    private CreateCustomer createCustomer;
    private ClientsV3Entity clientEntity;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        clientEntity = new ClientsV3Entity();
        clientEntity.setEmailAddress(Constants.EMAIL);

        createCustomer = new CreateCustomer();
        createCustomer.setIdClient(Constants.ID_CLIENT);
    }

    @Test
    public void shouldCreateCustomerAndUpdateCustomerCreatedStatus() {
        when(clientsV3Repository.findByIdClient(textCaptor.capture())).thenReturn(Option.of(clientEntity));
        when(customerService.createUserCustomer(anyMap(), any())).thenReturn(Try.success(true));

        Try<Boolean> response = createCustomerUseCase.execute(createCustomer);

        verify(clientsV3Repository).save(clientEntityCaptor.capture());

        assertTrue(response.isSuccess());
        assertTrue(response.get());
        assertEquals(textCaptor.getAllValues().get(0), createCustomer.getIdClient());
        assertTrue(clientEntityCaptor.getValue().isCustomerCreatedStatus());
    }

    @Test
    public void shouldNotCreateCustomerAndNotUpdateCustomerCreatedStatus() {
        when(clientsV3Repository.findByIdClient(anyString())).thenReturn(Option.of(clientEntity));
        when(customerService.createUserCustomer(anyMap(), any())).thenReturn(Try.failure(new CreateCustomerException("Error")));

        Try<Boolean> response = createCustomerUseCase.execute(createCustomer);

        verify(clientsV3Repository,times(0)).save(clientEntityCaptor.capture());

        assertTrue(response.isFailure());
        assertTrue(response.getCause() instanceof CreateCustomerException);
    }
}
