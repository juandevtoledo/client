package com.lulobank.clients.starter.v3.adapters.in.sqs.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lulobank.clients.starter.v3.adapters.in.sqs.event.CreatePreApprovedOfferMessage;
import com.lulobank.clients.v3.adapters.port.in.CreatePreApprovedOffer.CreatePreApprovedOfferPort;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.lulobank.clients.starter.v3.adapters.in.sqs.factory.EntityFactory.DataPreApprovedOfferFactory.getDataPreApprovedOffer;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class CreatePreApprovedOfferHandlerTest {

    private static final String ID_CLIENT = "4664ec04-fdb1-44bf-b5fb-5230a1f05519";
    private static final Integer MAX_TOTAL_AMOUNT =7000000;

    @Mock
    private  CreatePreApprovedOfferPort createPreApprovedOfferPort;

    @InjectMocks
    private CreatePreApprovedOfferHandler createPreApprovedOfferHandler;

    @Captor
    private ArgumentCaptor<ClientsV3Entity> captorRequest;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void processDataPreApprovedOfferOk() throws JsonProcessingException {
        when(createPreApprovedOfferPort.execute(captorRequest.capture())).thenReturn(Try.run(System.out::println));
        Try<Void> response = createPreApprovedOfferHandler.execute(buildEvent());
        assertThat(response.isSuccess(), is(true));
        assertThat(captorRequest.getValue().getIdClient(), is(ID_CLIENT));
        assertThat(captorRequest.getValue().getValue(), is(MAX_TOTAL_AMOUNT));
    }

    @Test
    public void processDataPreApprovedOfferFailed() throws JsonProcessingException {
        when(createPreApprovedOfferPort.execute(captorRequest.capture())).thenReturn(Try.failure(new Exception()));
        Try<Void> response = createPreApprovedOfferHandler.execute(buildRequestFailed());
        assertThat(response.isFailure(), is(true));
    }

    private CreatePreApprovedOfferMessage buildRequestFailed() {
        CreatePreApprovedOfferMessage preApprovedOffer = new CreatePreApprovedOfferMessage();
        preApprovedOffer.setIdClient(null);
        preApprovedOffer.setMaxTotalAmount(null);
        return preApprovedOffer;
    }

    private CreatePreApprovedOfferMessage buildEvent() throws JsonProcessingException {
        return getDataPreApprovedOffer();
    }

}