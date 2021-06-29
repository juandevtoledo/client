package com.lulobank.clients.starter.adapter.out;

import com.lulobank.clients.sdk.operations.exception.ClientsServiceException;
import com.lulobank.clients.services.application.port.out.reporting.ReportingPort;
import com.lulobank.clients.starter.outboundadapter.digitalevidence.DigitalEvidenceServiceAdapter;
import com.lulobank.clients.v3.util.DigitalEvidenceTypes;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;

import static com.lulobank.clients.starter.adapter.Sample.clientEntityV3Builder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;

public class DigitalEvidenceServiceAdapterTest {

    @Mock
    private ReportingPort reportingPort;

    private DigitalEvidenceServiceAdapter testedClass;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        testedClass = new DigitalEvidenceServiceAdapter(reportingPort);
    }

    @Test
    public void saveDigitalEvidenceApp(){
        when(reportingPort.createDigitalEvidence(anyMap(), any(), any()))
                .thenReturn(Try.success(Boolean.TRUE));
        Try<Boolean> response = testedClass.saveDigitalEvidence(new HashMap<>(), clientEntityV3Builder(), DigitalEvidenceTypes.APP);
        assertEquals(response.get(), true);
    }

    @Test
    public void saveDigitalEvidenceSavingAccount(){
        when(reportingPort.createDigitalEvidence(anyMap(), any(), any()))
                .thenReturn(Try.success(Boolean.TRUE));
        Try<Boolean> response = testedClass.saveDigitalEvidence(new HashMap<>(), clientEntityV3Builder(), DigitalEvidenceTypes.SAVINGS_ACCOUNT);
        assertEquals(response.get(), true);
    }

    @Test
    public void saveDigitalEvidenceCreditsAccount(){
        when(reportingPort.createDigitalEvidence(anyMap(), any(), any()))
                .thenReturn(Try.success(Boolean.TRUE));
        Try<Boolean> response = testedClass.saveDigitalEvidence(new HashMap<>(), clientEntityV3Builder(), DigitalEvidenceTypes.CREDIT_ACCOUNT);
        assertEquals(response.get(), true);
    }

    @Test
    public void saveDigitalEvidenceAppFailed(){
        when(reportingPort.createDigitalEvidence(anyMap(), any(), any()))
                .thenReturn(Try.failure(new ClientsServiceException("Error", 500)));
        Try<Boolean> response = testedClass.saveDigitalEvidence(new HashMap<>(), clientEntityV3Builder(), DigitalEvidenceTypes.APP);
        assertTrue(response.isFailure());
    }

    @Test
    public void saveDigitalEvidenceSavingAccountFailed(){
        when(reportingPort.createDigitalEvidence(anyMap(), any(), any()))
                .thenReturn(Try.failure(new ClientsServiceException("Error", 500)));
        Try<Boolean> response = testedClass.saveDigitalEvidence(new HashMap<>(), clientEntityV3Builder(), DigitalEvidenceTypes.SAVINGS_ACCOUNT);
        assertTrue(response.isFailure());
    }

}
