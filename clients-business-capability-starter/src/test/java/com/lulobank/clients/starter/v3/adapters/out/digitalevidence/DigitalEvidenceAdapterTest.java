package com.lulobank.clients.starter.v3.adapters.out.digitalevidence;

import co.com.lulobank.tracing.restTemplate.HttpError;
import co.com.lulobank.tracing.restTemplate.RestTemplateClient;
import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.starter.adapter.out.digitalevidence.DigitalEvidenceAdapter;
import com.lulobank.clients.starter.adapter.out.digitalevidence.dto.AppAcceptanceDocumentsResponse;
import com.lulobank.clients.starter.adapter.out.digitalevidence.dto.DigitalEvidenceResponse;
import com.lulobank.clients.starter.v3.adapters.out.Sample;
import com.lulobank.clients.v3.adapters.port.out.digitalevidence.dto.AcceptancesDocumentRequest;
import com.lulobank.clients.v3.adapters.port.out.digitalevidence.dto.AppAcceptanceDocuments;
import com.lulobank.clients.v3.adapters.port.out.digitalevidence.dto.DigitalEvidenceDocuments;
import io.vavr.control.Either;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;

import static com.lulobank.clients.starter.adapter.Constant.ID_CLIENT;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class DigitalEvidenceAdapterTest {

    private DigitalEvidenceAdapter testedClass;

    @Mock
    private RestTemplateClient digitalEvidenceRestTemplateClient;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        testedClass = new DigitalEvidenceAdapter(digitalEvidenceRestTemplateClient);
    }

    @Test
    public void saveDigitalEvidenceShouldReturnOk(){
        DigitalEvidenceResponse digitalEvidenceResponse = new DigitalEvidenceResponse();
        digitalEvidenceResponse.setResponse(true);
        when(digitalEvidenceRestTemplateClient.post(any(), any(), any(), eq(DigitalEvidenceResponse.class)))
                .thenReturn(Either.right(new ResponseEntity<>(digitalEvidenceResponse, HttpStatus.OK)));
        Either<UseCaseResponseError, DigitalEvidenceDocuments> response =
                testedClass.saveDigitalEvidence(new HashMap<>(), ID_CLIENT, Sample.getDigitalEvidenceRequest());
        assertTrue(response.isRight());
        assertThat(response.get().getResponse(), is(true));
    }

    @Test
    public void saveDigitalEvidenceShouldReturnError(){
        when(digitalEvidenceRestTemplateClient.post(any(), any(), any(), eq(DigitalEvidenceResponse.class)))
                .thenReturn(Either.left(new HttpError("500", "Error Consuming service", null)));
        Either<UseCaseResponseError, DigitalEvidenceDocuments> response =
                testedClass.saveDigitalEvidence(new HashMap<>(), ID_CLIENT, Sample.getDigitalEvidenceRequest());
        assertTrue(response.isLeft());
    }

    @Test
    public void createAcceptancesDocumentShouldReturnOk(){
        AppAcceptanceDocumentsResponse appAcceptanceDocumentsResponse = new AppAcceptanceDocumentsResponse();
        appAcceptanceDocumentsResponse.setResponse(Boolean.TRUE);
        when(digitalEvidenceRestTemplateClient.post(any(), any(), any(), eq(AppAcceptanceDocumentsResponse.class)))
                .thenReturn(Either.right(new ResponseEntity<>(appAcceptanceDocumentsResponse, HttpStatus.OK)));
        Either<UseCaseResponseError, AppAcceptanceDocuments> response =
                testedClass.createAcceptancesDocument(new HashMap<>(), ID_CLIENT, AcceptancesDocumentRequest.builder()
                        .documentAcceptancesTimestamp("11-11-2020")
                        .build());
        assertTrue(response.isRight());
        assertThat(response.get().getResponse(), is(true));
    }

    @Test
    public void createAcceptancesDocumentShouldReturnError(){
        when(digitalEvidenceRestTemplateClient.post(any(), any(), any(), eq(AppAcceptanceDocumentsResponse.class)))
                .thenReturn(Either.left(new HttpError("500", "Error Consuming service", null)));
        Either<UseCaseResponseError, AppAcceptanceDocuments> response =
                testedClass.createAcceptancesDocument(new HashMap<>(), ID_CLIENT, AcceptancesDocumentRequest.builder()
                        .documentAcceptancesTimestamp("11-11-2020")
                        .build());
        assertTrue(response.isLeft());
    }

}
