package com.lulobank.clients.starter.v3.adapters.in.sqs.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lulobank.clients.services.domain.DocumentType;
import com.lulobank.clients.services.domain.activateselfcertifiedpepclient.ActivatePepClientRequest;
import com.lulobank.clients.starter.v3.adapters.in.sqs.event.ActivateSelfCertifiedPEPClient;
import com.lulobank.clients.v3.adapters.port.in.activateselfcertifiedpepclient.ActivatePepClientPort;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static com.lulobank.clients.starter.v3.adapters.in.sqs.factory.EntityFactory.ActivateSelfCertifiedPEPClientFactory.getActivateSelfCertifiedPEPClient;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class ActivatePepClientHandlerTest {


    private static final String CARD_ID = "123";
    private static final LocalDateTime WHITELIST_EXPIRATION = LocalDateTime.parse("2020-11-25T00:00:00");

    @Mock
    private ActivatePepClientPort activatePEPClientPort;
    @InjectMocks
    private ActivatePepClientHandler activatePEPClientHandler;

    @Captor
    private ArgumentCaptor<ActivatePepClientRequest> captorRequest;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void processActivateSelfCertifiedPEPClientOk() throws JsonProcessingException {
        when(activatePEPClientPort.execute(captorRequest.capture())).thenReturn(Try.run(System.out::println));
        Try<Void> response = activatePEPClientHandler.execute(buildEvent());
        assertThat(response.isSuccess(), is(true));
        assertThat(captorRequest.getValue().getClientPersonalInformation().getDocument().getCardId(), is(CARD_ID));
        assertThat(captorRequest.getValue().getClientPersonalInformation().getDocument().getDocumentType(),
                is(DocumentType.CC.name()));
        assertThat(captorRequest.getValue().isWhitelisted(), is(true));
        assertThat(captorRequest.getValue().getWhitelistExpirationDate(), is(WHITELIST_EXPIRATION));
    }

    @Test
    public void processActivateSelfCertifiedPEPClientFail() throws JsonProcessingException {
        when(activatePEPClientPort.execute(captorRequest.capture())).thenReturn(Try.failure(new RuntimeException()));
        Try<Void> response = activatePEPClientHandler.execute(buildEvent());
        assertThat(response.isFailure(), is(true));
        assertThat(captorRequest.getValue().getClientPersonalInformation().getDocument().getCardId(), is(CARD_ID));
        assertThat(captorRequest.getValue().getClientPersonalInformation().getDocument().getDocumentType(),
                is(DocumentType.CC.name()));
        assertThat(captorRequest.getValue().isWhitelisted(), is(true));
        assertThat(captorRequest.getValue().getWhitelistExpirationDate(), is(WHITELIST_EXPIRATION));
    }

    private ActivateSelfCertifiedPEPClient buildEvent() throws JsonProcessingException {
        return getActivateSelfCertifiedPEPClient();
    }
}
