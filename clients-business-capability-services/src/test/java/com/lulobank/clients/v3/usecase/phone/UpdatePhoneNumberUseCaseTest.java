package com.lulobank.clients.v3.usecase.phone;

import com.lulobank.clients.services.application.util.HttpDomainStatus;
import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.ports.repository.ClientsRepositoryV2;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.error.ClientsDataError;
import com.lulobank.clients.v3.error.ClientsDataErrorStatus;
import com.lulobank.clients.v3.usecase.command.UpdatePhoneNumber;
import io.vavr.control.Either;
import io.vavr.control.Option;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static com.lulobank.clients.services.Constants.ID_CLIENT;
import static com.lulobank.clients.services.Constants.PHONE;
import static com.lulobank.clients.services.Constants.PREFIX;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

public class UpdatePhoneNumberUseCaseTest {

    @Mock
    private ClientsV3Repository clientsV3Repository;
    @Mock
    private ClientsRepositoryV2 clientsRepositoryV2;

    private UpdatePhoneNumberUseCase target;

    private UpdatePhoneNumber command;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        target = new UpdatePhoneNumberUseCase(clientsV3Repository, clientsRepositoryV2);
        command = UpdatePhoneNumber.builder()
                .idClient(ID_CLIENT)
                .newPhoneNumber(PHONE)
                .countryCode(PREFIX).build();
    }

    @Test
    public void phoneAlreadyExist() {

        when(clientsRepositoryV2.findByPhonePrefixAndPhoneNumber(PREFIX, PHONE)).thenReturn(Option.of(new ClientEntity()));

        Either<UseCaseResponseError, Boolean> response = target.execute(command);
        Assert.assertFalse(response.isRight());
        Assert.assertTrue(response.isLeft());
        Assert.assertTrue(response.getLeft() instanceof ClientsDataError);
        Assert.assertEquals(ClientsDataErrorStatus.CLI_108.name(), response.getLeft().getBusinessCode());
        Assert.assertEquals(ClientsDataErrorStatus.VALIDATION_DETAIL, response.getLeft().getDetail());
        Assert.assertEquals(String.valueOf(HttpDomainStatus.CONFLICT.value()), response.getLeft().getProviderCode());
        Mockito.verify(clientsV3Repository, times(0)).updatePhoneNumber(any(), any(), any());
        Mockito.verify(clientsRepositoryV2, times(1)).findByPhonePrefixAndPhoneNumber(any(), any());

    }

    @Test
    public void phoneDoesNotExist() {

        when(clientsRepositoryV2.findByPhonePrefixAndPhoneNumber(PREFIX, PHONE)).thenReturn(Option.none());
        when(clientsV3Repository.updatePhoneNumber(ID_CLIENT, PHONE, PREFIX)).thenReturn(Either.right(true));

        Either<UseCaseResponseError, Boolean> response = target.execute(command);
        Assert.assertTrue(response.isRight());
        Assert.assertFalse(response.isLeft());
        Assert.assertTrue(response.get());
        Mockito.verify(clientsV3Repository, times(1)).updatePhoneNumber(any(), any(), any());
        Mockito.verify(clientsRepositoryV2, times(1)).findByPhonePrefixAndPhoneNumber(any(), any());

    }
}
