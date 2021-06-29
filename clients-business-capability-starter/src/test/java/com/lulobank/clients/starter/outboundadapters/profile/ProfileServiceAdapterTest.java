package com.lulobank.clients.starter.outboundadapters.profile;

import co.com.lulobank.tracing.restTemplate.HttpError;
import co.com.lulobank.tracing.restTemplate.RestTemplateClient;
import com.lulobank.clients.starter.outboundadapter.profile.ProfileServiceAdapter;
import io.vavr.control.Either;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;

import static com.lulobank.clients.starter.adapter.Constant.COUNTRY_CODE;
import static com.lulobank.clients.starter.adapter.Constant.ID_CLIENT;
import static com.lulobank.clients.starter.adapter.Constant.MAIL;
import static com.lulobank.clients.starter.adapter.Constant.PHONE_NUMBER;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class ProfileServiceAdapterTest {

    @Mock
    private RestTemplateClient clientsRestTemplateClient;

    private ProfileServiceAdapter target;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        target= new ProfileServiceAdapter(clientsRestTemplateClient);
    }

    @Test
    public void serviceReturnInternalError(){
        when(clientsRestTemplateClient.put(anyString(), any(),any(),any()))
                .thenReturn(Either.left(new HttpError("500","",new HashMap<>())));

        Try<Boolean> response = target.savePhoneNumberAndEmail(new HashMap<>(), ID_CLIENT, MAIL, PHONE_NUMBER, COUNTRY_CODE);
        assertTrue(response.isFailure());

    }

    @Test
    public void serviceReturnNotContent(){
        when(clientsRestTemplateClient.put(anyString(), any(),any(),any()))
                .thenReturn(Either.right(ResponseEntity.noContent().build()));

        Try<Boolean> response = target.savePhoneNumberAndEmail(new HashMap<>(), ID_CLIENT, MAIL, PHONE_NUMBER, COUNTRY_CODE);
        assertFalse(response.isFailure());
        assertTrue(response.isSuccess());
        assertTrue(response.get());

    }
}
