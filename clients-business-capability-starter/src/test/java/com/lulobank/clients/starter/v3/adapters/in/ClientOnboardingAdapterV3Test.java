package com.lulobank.clients.starter.v3.adapters.in;

import com.lulobank.clients.starter.v3.adapters.in.dto.ChangeProductResponse;
import com.lulobank.clients.v3.usecase.ChangeProductSavingUseCase;
import com.lulobank.clients.v3.usecase.command.ChangeProductResponseError;
import com.lulobank.clients.v3.usecase.command.ChangeProductSaving;
import com.lulobank.clients.v3.vo.AdapterCredentials;
import io.vavr.control.Either;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ClientOnboardingAdapterV3Test {

    @Mock
    private ChangeProductSavingUseCase changeProductSavingUseCase;
    private String idClient;
    private ClientOnboardingAdapterV3 testClass;
    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        testClass=new ClientOnboardingAdapterV3(changeProductSavingUseCase);
        idClient= UUID.randomUUID().toString();
    }

    @Test
    public void changedProduct(){
        when(changeProductSavingUseCase.execute(any(ChangeProductSaving.class))).thenReturn(Either.right(true));
        ResponseEntity<ChangeProductResponse> responseEntity = testClass.changingProductToSaving(new HttpHeaders(),idClient);
        MatcherAssert.assertThat(responseEntity.getStatusCode(),is(HttpStatus.CREATED));
    }

    @Test
    public void changedProductErrorSinceUseCaseError(){
        when(changeProductSavingUseCase.execute(any(ChangeProductSaving.class))).thenReturn(Either.left(new ChangeProductResponseError("502","error service")));
        ResponseEntity<ChangeProductResponse> responseEntity = testClass.changingProductToSaving(new HttpHeaders(),idClient);
        MatcherAssert.assertThat(responseEntity.getStatusCode(),is(HttpStatus.BAD_GATEWAY));
    }


    public ChangeProductSaving changeProductSavingBuilder() {
        ChangeProductSaving changeProductSaving=new ChangeProductSaving();
        changeProductSaving.setIdClient(idClient);
        changeProductSaving.setCredentials(new AdapterCredentials(new HashMap<>()));
        return changeProductSaving;
    }


}
