package com.lulobank.clients.v3.usecase.createaddress;

import com.lulobank.clients.services.application.port.out.clientsdata.ClientsDataRepositoryPort;
import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.services.utils.HttpCodes;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.error.ClientsDataError;
import com.lulobank.clients.v3.usecase.command.ClientAddressData;
import io.vavr.control.Either;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.lulobank.clients.sdk.operations.util.CheckPoints.CLIENT_ADDRESS_FINISHED;
import static com.lulobank.clients.sdk.operations.util.CheckPoints.ON_BOARDING;
import static com.lulobank.clients.services.SamplesV3.buildClientAddressData;
import static com.lulobank.clients.services.SamplesV3.clientEntityV3Builder;
import static com.lulobank.clients.services.utils.ProductTypeEnum.CREDIT_ACCOUNT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ClientCreateAddressUseCaseTest {

    @Mock
    private ClientsDataRepositoryPort clientsDataRepositoryPort;
    private ClientCreateAddressUseCase clientCreateAddressUseCase;
    @Captor
    private ArgumentCaptor<ClientsV3Entity> clientsV3EntityArgumentCaptor;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        clientCreateAddressUseCase = new ClientCreateAddressUseCase(clientsDataRepositoryPort);
    }

    @Test
    public void clientAddressShouldBeCreatedForTheFirstTime(){
        ClientsV3Entity clientsV3Entity = clientEntityV3Builder();
        ClientAddressData clientAddressData = buildClientAddressData();
        when(clientsDataRepositoryPort.findByIdClient(clientsV3Entity.getIdClient()))
                .thenReturn(Either.right(clientsV3Entity));
        when(clientsDataRepositoryPort.save(clientsV3EntityArgumentCaptor.capture()))
                .thenReturn(Either.right(clientsV3Entity));

        Either<UseCaseResponseError, ClientAddressData> result = clientCreateAddressUseCase.execute(clientAddressData);

        assertTrue(result.isRight());
        assertEquals(clientAddressData.getAddress(), result.get().getAddress());
        assertEquals(CLIENT_ADDRESS_FINISHED.name(),clientsV3EntityArgumentCaptor.getValue().getOnBoardingStatus().getCheckpoint());
        assertEquals(CREDIT_ACCOUNT.name(),clientsV3EntityArgumentCaptor.getValue().getOnBoardingStatus().getProductSelected());
        verify(clientsDataRepositoryPort,times(1)).findByIdClient(clientsV3Entity.getIdClient());
        verify(clientsDataRepositoryPort,times(1)).save(clientsV3Entity);
    }

    @Test
    public void shouldReturnConflictWhenTheClientAlreadyHasAddress(){
        ClientsV3Entity clientsV3Entity = clientEntityV3Builder();
        ClientAddressData clientAddressData = buildClientAddressData();

        clientsV3Entity.setAddress(clientAddressData.getAddress());
        clientsV3Entity.setAddressPrefix(clientAddressData.getAddressPrefix());
        clientsV3Entity.setAddressComplement(clientAddressData.getAddressComplement());
        clientsV3Entity.setCity(clientAddressData.getCity());
        clientsV3Entity.setCityId(clientAddressData.getCityId());
        clientsV3Entity.setDepartment(clientAddressData.getDepartment());
        clientsV3Entity.setDepartmentId(clientAddressData.getDepartmentId());

        when(clientsDataRepositoryPort.findByIdClient(clientsV3Entity.getIdClient()))
                .thenReturn(Either.right(clientsV3Entity));


        Either<UseCaseResponseError, ClientAddressData> result = clientCreateAddressUseCase.execute(clientAddressData);

        assertTrue(result.isLeft());
        assertEquals(ON_BOARDING.name(),clientsV3Entity.getOnBoardingStatus().getCheckpoint());
        assertEquals(HttpCodes.CONFLICT, result.getLeft().getProviderCode());

        verify(clientsDataRepositoryPort,times(1)).findByIdClient(clientsV3Entity.getIdClient());
        verify(clientsDataRepositoryPort,times(0)).save(clientsV3Entity);
    }

    @Test
    public void shouldReturnNotFoundWhenTheClientDoesNotExist(){
        ClientsV3Entity clientsV3Entity = clientEntityV3Builder();
        ClientAddressData clientAddressData = buildClientAddressData();
        ClientsDataError expectedError = ClientsDataError.clientNotFound();
        when(clientsDataRepositoryPort.findByIdClient(clientAddressData.getIdClient()))
                .thenReturn(Either.left(expectedError));
        Either<UseCaseResponseError, ClientAddressData> result = clientCreateAddressUseCase.execute(clientAddressData);

        assertTrue(result.isLeft());
        assertEquals(ON_BOARDING.name(),clientsV3Entity.getOnBoardingStatus().getCheckpoint());
        result.peekLeft(error -> assertEquals(error.getBusinessCode(), expectedError.getBusinessCode()));
        verify(clientsDataRepositoryPort,times(1)).findByIdClient(clientsV3Entity.getIdClient());
        verify(clientsDataRepositoryPort,times(0)).save(clientsV3Entity);
    }

    @Test
    public void shouldReturnAnErrorWhenItFailsToSaveTheEntity(){
        ClientsV3Entity clientsV3Entity = clientEntityV3Builder();
        ClientAddressData clientAddressData = buildClientAddressData();
        ClientsDataError expectedError = ClientsDataError.internalError();

        when(clientsDataRepositoryPort.findByIdClient(clientAddressData.getIdClient()))
                .thenReturn(Either.right(clientsV3Entity));
        when(clientsDataRepositoryPort.save(clientsV3EntityArgumentCaptor.capture()))
                .thenReturn(Either.left(expectedError));

        Either<UseCaseResponseError, ClientAddressData> result = clientCreateAddressUseCase.execute(clientAddressData);

        assertTrue(result.isLeft());
        assertEquals(CLIENT_ADDRESS_FINISHED.name(),clientsV3EntityArgumentCaptor.getValue().getOnBoardingStatus().getCheckpoint());
        result.peekLeft(error -> assertEquals(error.getBusinessCode(), expectedError.getBusinessCode()));
        verify(clientsDataRepositoryPort,times(1)).findByIdClient(clientsV3Entity.getIdClient());
        verify(clientsDataRepositoryPort,times(1)).save(clientsV3Entity);
    }
}