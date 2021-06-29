package com.lulobank.clients.v3.usecase;

import com.lulobank.clients.services.Constants;
import com.lulobank.clients.services.SamplesV3;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.savingsaccount.SavingsAccountV3Service;
import com.lulobank.clients.v3.adapters.port.out.savingsaccount.dto.SavingsAccountResponse;
import com.lulobank.clients.v3.adapters.port.out.savingsaccount.error.SavingsAccountError;
import com.lulobank.clients.v3.usecase.command.ChangeProductResponseError;
import com.lulobank.clients.v3.usecase.command.ChangeProductSaving;
import com.lulobank.clients.v3.vo.AdapterCredentials;
import io.vavr.control.Either;
import io.vavr.control.Option;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class ChangeProductSavingUseCaseTest {

    @Mock
    private ClientsV3Repository clientsV3Repository;
    @Mock
    private SavingsAccountV3Service savingsAccountV3Service;

    ChangeProductSavingUseCase testClass;

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
        testClass=new ChangeProductSavingUseCase(clientsV3Repository,savingsAccountV3Service);
    }

    @Test
    public void changeProductSavings(){
        SavingsAccountResponse savingsAccountResponse=new SavingsAccountResponse();
        savingsAccountResponse.setIdCbs(Constants.ID_CBS);
        when(clientsV3Repository.findByIdClient(any())).thenReturn(Option.of(SamplesV3.clientEntityV3Builder()));
        when(savingsAccountV3Service.create(any(),anyMap())).thenReturn(Either.right(savingsAccountResponse));
        ChangeProductSaving changeProductSaving=new ChangeProductSaving();
        changeProductSaving.setIdClient(UUID.randomUUID().toString());
        changeProductSaving.setCredentials(new AdapterCredentials(new HashMap<>()));
        Either<ChangeProductResponseError, Boolean> response= testClass.execute(changeProductSaving);
        assertThat(response.isLeft(),is(false));
    }

    @Test
    public void errorChangeProductSavingsSinceServiceError(){
        when(clientsV3Repository.findByIdClient(any())).thenReturn(Option.of(SamplesV3.clientEntityV3Builder()));
        when(savingsAccountV3Service.create(any(),anyMap())).thenReturn(Either.left(savingsAccountErrorBuilder()));
        ChangeProductSaving changeProductSaving=new ChangeProductSaving();
        changeProductSaving.setCredentials(new AdapterCredentials(new HashMap<>()));
        Either<ChangeProductResponseError, Boolean> response= testClass.execute(changeProductSaving);
        assertThat(response.isLeft(),is(true));
        assertThat(response.getLeft().getCode(),is("502"));
        assertThat(response.getLeft().getMessage(),is("Error savings"));
    }

    public SavingsAccountError savingsAccountErrorBuilder() {
        SavingsAccountError savingsAccountError=new SavingsAccountError("502","detail","Error savings");
        return savingsAccountError;
    }
}
