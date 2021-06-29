package com.lulobank.clients.starter.outboundadapters.digitalevidence;

import co.com.lulobank.tracing.restTemplate.HttpError;
import co.com.lulobank.tracing.restTemplate.RestTemplateClient;
import com.lulobank.clients.starter.outboundadapter.digitalevidence.AcceptancesDocumentServiceAdapter;
import com.lulobank.reporting.sdk.operations.dto.AcceptancesDocument;
import io.vavr.control.Either;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static com.lulobank.clients.starter.v3.adapters.out.Sample.CLIENT_ID;
import static com.lulobank.clients.starter.v3.adapters.out.Sample.DATE;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class AcceptancesDocumentServiceAdapterTest {

    private static final String GET_SAVING_RESOURCE = "reporting/clients/%s/app-acceptances";

    @Mock
    private RestTemplateClient restTemplateClient;

    private AcceptancesDocumentServiceAdapter subject;

    private AcceptancesDocument acceptancesDocument;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        subject = new AcceptancesDocumentServiceAdapter(restTemplateClient);
        acceptancesDocument =new AcceptancesDocument();
        acceptancesDocument.setDocumentAcceptancesTimestamp(DATE);
        acceptancesDocument.setIdClient(CLIENT_ID);
    }
    @Test
    public void createDigitalEvidenceFail() {

        Map<String, String> auth = new HashMap<String, String>();
        String resource = String.format(GET_SAVING_RESOURCE, CLIENT_ID);
        when(restTemplateClient.post(resource,acceptancesDocument, auth, null))
                .thenReturn(Either.left(new HttpError("500", "Unxepected error trying to consume rest client", null)));
        Try<Boolean> response = subject.generateAcceptancesDocument(auth ,CLIENT_ID,acceptancesDocument);

        assertTrue(response.isFailure());
        assertFalse(response.isSuccess());

    }

    @Test
    public void getClientInformationShouldReturnLeft() {
        Map<String, String> auth = new HashMap<String, String>();
        String resource = String.format(GET_SAVING_RESOURCE, CLIENT_ID);
        when(restTemplateClient.post(resource,acceptancesDocument, auth, null))
                .thenReturn(Either.right(new ResponseEntity<>( HttpStatus.OK)));
        Try<Boolean> response = subject.generateAcceptancesDocument(auth ,CLIENT_ID,acceptancesDocument);

        assertTrue(response.isSuccess());
        assertFalse(response.isFailure());
    }

}
