package com.lulobank.clients.starter.adapter.out;

import com.amazonaws.SdkClientException;
import com.lulobank.clients.v3.error.ClientsDataError;
import com.lulobank.clients.v3.error.ClientsDataErrorStatus;
import com.lulobank.clients.starter.adapter.BaseUnitTest;
import com.lulobank.clients.starter.adapter.out.dynamodb.ClientsRepositoryAdapter;
import com.lulobank.clients.starter.v3.adapters.out.dynamo.dto.ClientEntity;
import com.lulobank.clients.starter.v3.mapper.ClientsEntityV3Mapper;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import io.vavr.control.Either;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.util.Optional;

import static com.lulobank.clients.starter.adapter.Constant.ID_CLIENT;
import static com.lulobank.clients.starter.adapter.Constant.MAIL;
import static com.lulobank.clients.starter.adapter.Sample.getClientEntity;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


public class ClientsRepositoryAdapterTest extends BaseUnitTest {
    @InjectMocks
    private ClientsRepositoryAdapter clientsRepositoryAdapter;

    @Test
    public void shouldReturnClientEntity() {

        ClientEntity clientEntity = getClientEntity();
        when(clientsDataRepository.findByEmailAddress(emailCaptor.capture())).thenReturn(Optional.of(clientEntity));

        Either<ClientsDataError, ClientsV3Entity> response = clientsRepositoryAdapter.findByEmailAddress(MAIL);

        assertEquals(MAIL, emailCaptor.getValue());
        assertTrue(response.isRight());
        assertEquals(ID_CLIENT, response.get().getIdClient());

    }

    @Test
    public void shouldReturnErrorSdkException() {
        when(clientsDataRepository.findByEmailAddress(emailCaptor.capture())).thenThrow(SdkClientException.class);

        Either<ClientsDataError, ClientsV3Entity> response = clientsRepositoryAdapter.findByEmailAddress(MAIL);

        assertEquals(MAIL, emailCaptor.getValue());
        assertTrue(response.isLeft());
        assertEquals(ClientsDataErrorStatus.CLI_100.name(), response.getLeft().getBusinessCode());
    }


    @Test
    public void shouldReturnNotFound() {
        when(clientsDataRepository.findByEmailAddress(emailCaptor.capture())).thenReturn(Optional.empty());

        Either<ClientsDataError, ClientsV3Entity> response = clientsRepositoryAdapter.findByEmailAddress(MAIL);

        assertEquals(MAIL, emailCaptor.getValue());
        assertTrue(response.isLeft());
        assertEquals(ClientsDataErrorStatus.CLI_101.name(), response.getLeft().getBusinessCode());
    }

    @Test
    public void shouldReturnEntityByIdClientOk() {
        ClientEntity clientEntity = getClientEntity();
        when(databaseBrave.queryOptional(any())).thenReturn(Optional.of((clientEntity)));

        Either<ClientsDataError, ClientsV3Entity> response = clientsRepositoryAdapter.findByIdClient(ID_CLIENT);

        assertTrue(response.isRight());
        assertEquals(clientEntity.getIdClient(), response.get().getIdClient());
        assertEquals(ID_CLIENT, response.get().getIdClient());
    }

    @Test
    public void shouldNotReturnEntityByIdClientWhenDoesNotExists() {
        when(databaseBrave.queryOptional(any())).thenReturn(Optional.empty());

        Either<ClientsDataError, ClientsV3Entity> result = clientsRepositoryAdapter.findByIdClient(ID_CLIENT);

        assertThat(result.isLeft(), is(true));
        ClientsDataError error = result.getLeft();
        assertThat(error, notNullValue());
        assertThat(error.getBusinessCode(), is(ClientsDataErrorStatus.CLI_101.name()));
        assertThat(error.getDetail(), is(ClientsDataErrorStatus.DEFAULT_DETAIL));
        assertThat(error.getProviderCode(), is("404"));
    }

    @Test
    public void shouldReturnClientEntityWhenSaveIsSuccess() {
        ClientEntity clientEntity = getClientEntity();
        ClientsV3Entity clientV3Entity = ClientsEntityV3Mapper.INSTANCE.toV3Entity(clientEntity);
        when(clientsDataRepository.save(any())).thenReturn(clientEntity);

        Either<ClientsDataError, ClientsV3Entity> response = clientsRepositoryAdapter.save(clientV3Entity);

        assertTrue(response.isRight());
        assertEquals(clientEntity.getIdCard(),response.get().getIdCard());
    }

    @Test
    public void shouldNotReturnClientEntityWhenSaveIsFail() {
        ClientEntity clientEntity = getClientEntity();
        ClientsV3Entity clientV3Entity = ClientsEntityV3Mapper.INSTANCE.toV3Entity(clientEntity);
        when(clientsDataRepository.save(any())).thenReturn(new Exception("Error saving entity"));

        Either<ClientsDataError, ClientsV3Entity> response = clientsRepositoryAdapter.save(clientV3Entity);

        assertTrue(response.isLeft());
    }
}
