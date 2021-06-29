package com.lulobank.clients.services.features.clientverificationresult;


import com.lulobank.clients.sdk.operations.util.CheckPoints;
import com.lulobank.clients.services.Sample;
import com.lulobank.clients.services.domain.RiskLevelBlackList;
import com.lulobank.clients.services.domain.StateBlackList;
import com.lulobank.clients.services.events.BlacklistResult;
import com.lulobank.clients.services.events.ClientPersonalInformationResult;
import com.lulobank.clients.services.events.ClientVerificationResult;
import com.lulobank.clients.services.events.IdDocument;
import com.lulobank.clients.services.exception.DigitalEvidenceException;
import com.lulobank.clients.services.inboundadapters.AbstractBaseUnitTest;
import com.lulobank.clients.services.ports.out.DigitalEvidenceService;
import com.lulobank.clients.services.utils.BiometricResultCodes;
import com.lulobank.clients.services.utils.ClientVerificationFirebase;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.IdentityBiometricV3;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.OnBoardingStatusV3;
import com.lulobank.core.events.Event;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static com.lulobank.clients.services.Constants.DATE_ISSUE;
import static com.lulobank.clients.services.Constants.GENDER;
import static com.lulobank.clients.services.Constants.ID_CARD;
import static com.lulobank.clients.services.Constants.ID_TRANSACTION;
import static com.lulobank.clients.services.Constants.LAST_NAME;
import static com.lulobank.clients.services.Constants.NAME;
import static com.lulobank.clients.services.SamplesV3.clientEntityV3Builder;
import static com.lulobank.clients.services.SamplesV3.identityBiometricV3Builder;
import static com.lulobank.clients.services.SamplesV3.onBoardingStatusV3Builder;
import static com.lulobank.clients.services.SamplesV3.transactionStateV3Builder;
import static com.lulobank.clients.services.domain.StateBlackList.BIOMETRY_FAILED;
import static com.lulobank.clients.services.domain.StateBlackList.BLACKLISTED;
import static com.lulobank.clients.services.domain.StateBlackList.NON_BLACKLISTED;
import static com.lulobank.clients.services.domain.StateBlackList.STARTED;
import static com.lulobank.clients.services.utils.BiometricResultCodes.FAILED_CAPTURE;
import static com.lulobank.clients.services.utils.BiometricResultCodes.FAKE_DOCUMENT;
import static com.lulobank.clients.services.utils.BiometricResultCodes.INCONSISTENCY_WITH_FACIAL_COMPARE;
import static com.lulobank.clients.services.utils.BiometricResultCodes.SUCCESSFUL;
import static com.lulobank.clients.services.utils.IdentityBiometricStatus.ALREADY_CLIENT_EXISTS;
import static com.lulobank.clients.services.utils.IdentityBiometricStatus.IN_PROGRESS;
import static com.lulobank.clients.services.utils.ProductTypeEnum.SAVING_ACCOUNT;
import static com.lulobank.clients.services.utils.StatusClientVerificationFirebaseEnum.KO_IDENTITY;
import static com.lulobank.clients.services.utils.StatusClientVerificationFirebaseEnum.KO_IDENTITY_FRAUD;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ClientVerificationResultHandlerTest extends AbstractBaseUnitTest {

    private ClientVerificationResultHandler testClass;

    @Mock
    private DigitalEvidenceService digitalEvidenceService;

    @Captor
    private ArgumentCaptor<ClientVerificationResult> clientVerificationResultCaptor;

    private final Try<Void> doNothingMethod = Try.run(() -> {
    });

    @Override
    protected void init() {
        testClass = new ClientVerificationResultHandler(clientsOutboundAdapter, mobileRetriesOption
                , clientsV3Repository, nonBlacklistedProcessService, blacklistedProcessService, reportingMessagingPort, digitalEvidenceService);
    }

    @Test
    public void statusOkNonBlacklistedEvent() {
        OnBoardingStatusV3 onBoardingStatus = onBoardingStatusV3Builder(SAVING_ACCOUNT);
        IdentityBiometricV3 identityBiometric = identityBiometricV3Builder(ID_TRANSACTION, IN_PROGRESS.name());
        ClientsV3Entity clientEntity = clientEntityV3Builder(onBoardingStatus, identityBiometric);
        when(clientsV3Repository.findByIdentityBiometric(any(IdentityBiometricV3.class)))
                .thenReturn(Option.of(clientEntity));
        when(clientsV3Repository.findByIdCard(any()))
                .thenReturn(Option.none());
        doNothing().when(nonBlacklistedProcessService)
                .emitEvent(any(ClientsV3Entity.class), clientVerificationResultCaptor.capture());
        when(reportingMessagingPort.sendBlacklistedDocuments(any())).thenReturn(doNothingMethod);
        when(digitalEvidenceService.saveDigitalEvidence(anyMap(),any(),any())).thenReturn(Try.success(Boolean.TRUE));
        testClass.start(getEvent().getPayload());
        verify(digitalEvidenceService, times(1)).saveDigitalEvidence(anyMap(), any(), any());
        verify(clientsV3Repository, times(1)).findByIdentityBiometric(any(IdentityBiometricV3.class));
        verify(clientsV3Repository, times(1)).findByIdCard(any());
        verify(nonBlacklistedProcessService, times(1)).emitEvent(any(ClientsV3Entity.class),
                any(ClientVerificationResult.class));
        verify(blacklistedProcessService, times(0)).rejectClient(any(ClientsV3Entity.class),
                any(ClientVerificationResult.class));
        verify(reportingMessagingPort, times(1)).sendBlacklistedDocuments(any());
        assertEquals(getEvent().getPayload().getStatus(), clientVerificationResultCaptor.getValue().getStatus());
        assertEquals(NON_BLACKLISTED.name(), clientVerificationResultCaptor.getValue().getBlacklist().getStatus());
    }

    @Test
    public void statusDigitalEvidenceFailed() {
        OnBoardingStatusV3 onBoardingStatus = onBoardingStatusV3Builder(SAVING_ACCOUNT);
        IdentityBiometricV3 identityBiometric = identityBiometricV3Builder(ID_TRANSACTION, IN_PROGRESS.name());
        ClientsV3Entity clientEntity = clientEntityV3Builder(onBoardingStatus, identityBiometric);
        when(clientsV3Repository.findByIdentityBiometric(any(IdentityBiometricV3.class)))
                .thenReturn(Option.of(clientEntity));
        when(clientsV3Repository.findByIdCard(any()))
                .thenReturn(Option.none());
        doNothing().when(nonBlacklistedProcessService)
                .emitEvent(any(ClientsV3Entity.class), clientVerificationResultCaptor.capture());
        when(reportingMessagingPort.sendBlacklistedDocuments(any())).thenReturn(doNothingMethod);
        when(digitalEvidenceService.saveDigitalEvidence(anyMap(),any(),any()))
                .thenReturn(Try.failure(new DigitalEvidenceException("Error")));
        testClass.start(getEvent().getPayload());
        verify(digitalEvidenceService, times(1)).saveDigitalEvidence(anyMap(), any(), any());
        verify(clientsV3Repository, times(1)).findByIdentityBiometric(any(IdentityBiometricV3.class));
        verify(clientsV3Repository, times(1)).findByIdCard(any());
        verify(nonBlacklistedProcessService, times(1)).emitEvent(any(ClientsV3Entity.class),
                any(ClientVerificationResult.class));
        verify(blacklistedProcessService, times(0)).rejectClient(any(ClientsV3Entity.class),
                any(ClientVerificationResult.class));
        verify(reportingMessagingPort, times(1)).sendBlacklistedDocuments(any());
        assertEquals(getEvent().getPayload().getStatus(), clientVerificationResultCaptor.getValue().getStatus());
        assertEquals(NON_BLACKLISTED.name(), clientVerificationResultCaptor.getValue().getBlacklist().getStatus());
    }

    @Test
    public void idCardExist() {
        OnBoardingStatusV3 onBoardingStatus = onBoardingStatusV3Builder(SAVING_ACCOUNT);
        IdentityBiometricV3 identityBiometric = identityBiometricV3Builder(ID_TRANSACTION, IN_PROGRESS.name());
        ClientsV3Entity clientEntity = clientEntityV3Builder(onBoardingStatus, identityBiometric);
        when(clientsV3Repository.findByIdentityBiometric(any(IdentityBiometricV3.class)))
                .thenReturn(Option.of(clientEntity));
        when(databaseReference.child(anyString())).thenReturn(databaseReference);
        ClientsV3Entity clientEntityExist = clientEntityV3Builder(onBoardingStatus, identityBiometric);
        when(clientsV3Repository.findByIdCard(anyString()))
                .thenReturn(Option.of(clientEntityExist));
        Event<ClientVerificationResult> event = getEvent();
        event.getPayload().getBlacklist().setStatus(STARTED.name());
        testClass.start(event.getPayload());
        verify(clientsV3Repository, times(1)).save(clientEntityV3ArgumentCaptor.capture());
        ClientsV3Entity clientEntitySave = clientEntityV3ArgumentCaptor.getValue();
        assertEquals("Identity status Save Right", ALREADY_CLIENT_EXISTS.name(), clientEntitySave.getIdentityBiometric().getStatus());
    }

    @Test
    public void idCardExistNotStarted() {
        OnBoardingStatusV3 onBoardingStatus = onBoardingStatusV3Builder(SAVING_ACCOUNT);
        IdentityBiometricV3 identityBiometric = identityBiometricV3Builder(ID_TRANSACTION, IN_PROGRESS.name());
        ClientsV3Entity clientEntity = clientEntityV3Builder(onBoardingStatus, identityBiometric);
        when(clientsV3Repository.findByIdentityBiometric(any(IdentityBiometricV3.class)))
                .thenReturn(Option.of(clientEntity));
        when(clientsV3Repository.findByIdCard(anyString()))
                .thenReturn(Option.of(clientEntity));
        when(clientsV3Repository.save(any()))
                .thenReturn(Try.of(() -> clientEntity));
        doNothing().when(nonBlacklistedProcessService)
                .emitEvent(any(ClientsV3Entity.class), clientVerificationResultCaptor.capture());
        when(reportingMessagingPort.sendBlacklistedDocuments(any())).thenReturn(doNothingMethod);
        when(digitalEvidenceService.saveDigitalEvidence(anyMap(),any(),any())).thenReturn(Try.success(Boolean.TRUE));
        Event<ClientVerificationResult> event = getEvent();
        event.getPayload().getBlacklist().setStatus(NON_BLACKLISTED.name());
        testClass.start(event.getPayload());
        verify(digitalEvidenceService, times(1)).saveDigitalEvidence(anyMap(), any(), any());
        verify(clientsV3Repository, times(1)).findByIdentityBiometric(any(IdentityBiometricV3.class));
        verify(clientsV3Repository, times(1)).findByIdCard(any());
        verify(nonBlacklistedProcessService, times(1)).emitEvent(any(ClientsV3Entity.class),
                any(ClientVerificationResult.class));
        verify(blacklistedProcessService, times(0)).rejectClient(any(ClientsV3Entity.class),
                any(ClientVerificationResult.class));
        verify(reportingMessagingPort, times(1)).sendBlacklistedDocuments(any());
        assertEquals(getEvent().getPayload().getStatus(), clientVerificationResultCaptor.getValue().getStatus());
        assertEquals(NON_BLACKLISTED.name(), clientVerificationResultCaptor.getValue().getBlacklist().getStatus());
    }

    @Test
    public void idCardDoNotExistNotStarted() {
        OnBoardingStatusV3 onBoardingStatus = onBoardingStatusV3Builder(SAVING_ACCOUNT);
        IdentityBiometricV3 identityBiometric = identityBiometricV3Builder(ID_TRANSACTION, IN_PROGRESS.name());
        ClientsV3Entity clientEntity = clientEntityV3Builder(onBoardingStatus, identityBiometric);
        when(clientsV3Repository.findByIdentityBiometric(any(IdentityBiometricV3.class)))
                .thenReturn(Option.of(clientEntity));
        when(clientsV3Repository.findByIdCard(anyString()))
                .thenReturn(Option.none());
        doNothing().when(blacklistedProcessService)
                .rejectClient(any(ClientsV3Entity.class), clientVerificationResultCaptor.capture());
        when(reportingMessagingPort.sendBlacklistedDocuments(any())).thenReturn(doNothingMethod);
        when(digitalEvidenceService.saveDigitalEvidence(anyMap(),any(),any())).thenReturn(Try.success(Boolean.TRUE));
        Event<ClientVerificationResult> event = getEvent();
        event.getPayload().getBlacklist().setStatus(BLACKLISTED.name());
        event.getPayload().getBlacklist().setResultRiskLevel(RiskLevelBlackList.HIGH_RISK.getLevel());
        event.getPayload().getBlacklist().setReportDate(LocalDateTime.now().toString());
        testClass.start(event.getPayload());
        verify(digitalEvidenceService, times(1)).saveDigitalEvidence(anyMap(), any(), any());
        verify(clientsV3Repository, times(1)).findByIdentityBiometric(any(IdentityBiometricV3.class));
        verify(clientsV3Repository, times(1)).findByIdCard(any());
        verify(nonBlacklistedProcessService, times(0)).emitEvent(any(ClientsV3Entity.class),
                any(ClientVerificationResult.class));
        verify(blacklistedProcessService, times(1)).rejectClient(any(ClientsV3Entity.class),
                any(ClientVerificationResult.class));
        verify(reportingMessagingPort, times(1)).sendBlacklistedDocuments(any());
        assertEquals(getEvent().getPayload().getStatus(), clientVerificationResultCaptor.getValue().getStatus());
        assertEquals(BLACKLISTED.name(), clientVerificationResultCaptor.getValue().getBlacklist().getStatus());
        assertEquals(RiskLevelBlackList.HIGH_RISK.getLevel(), clientVerificationResultCaptor.getValue().getBlacklist().getResultRiskLevel());
    }

    @Test
    public void biometricFailed() {
        OnBoardingStatusV3 onBoardingStatus = onBoardingStatusV3Builder(SAVING_ACCOUNT);
        IdentityBiometricV3 identityBiometric = identityBiometricV3Builder(ID_TRANSACTION, IN_PROGRESS.name());
        identityBiometric.setTransactionState(transactionStateV3Builder(SUCCESSFUL));
        ClientsV3Entity clientEntity = clientEntityV3Builder(onBoardingStatus, identityBiometric);
        clientEntity.setIdCard(null);
        when(clientsV3Repository.findByIdCard(anyString()))
                .thenReturn(Option.none());
        when(clientsV3Repository.findByIdentityBiometric(any(IdentityBiometricV3.class)))
                .thenReturn(Option.of(clientEntity));
        when(databaseReference.child(anyString())).thenReturn(databaseReference);
        ClientVerificationResult payload = eventByFailedStatus(FAILED_CAPTURE).getPayload();
        payload.getBlacklist().setStatus(BIOMETRY_FAILED.name());
        testClass.start(payload);
        verify(databaseReference, times(1)).updateChildrenAsync(firebaseUpdateCaptor.capture());
        verify(clientsV3Repository, times(1)).save(clientEntityV3ArgumentCaptor.capture());
        ClientVerificationFirebase firebaseUpdateValue =
                (ClientVerificationFirebase) firebaseUpdateCaptor.getValue().get("clientVerification");
        ClientsV3Entity clientEntitySave = clientEntityV3ArgumentCaptor.getValue();
        assertEquals("Firebase update ok", KO_IDENTITY.name(), firebaseUpdateValue.getVerificationResult());
        assertEquals("CheckPoint was not update", CheckPoints.ON_BOARDING.name(),
                clientEntitySave.getOnBoardingStatus().getCheckpoint());
        assertTrue("IdCard is empty", Objects.isNull(clientEntitySave.getIdCard()));
    }

    @Test
    public void BiometricFailedFraud() {
        OnBoardingStatusV3 onBoardingStatus = onBoardingStatusV3Builder(SAVING_ACCOUNT);
        IdentityBiometricV3 identityBiometric = identityBiometricV3Builder(ID_TRANSACTION, IN_PROGRESS.name());
        identityBiometric.setTransactionState(transactionStateV3Builder(FAKE_DOCUMENT));
        ClientsV3Entity clientEntity = clientEntityV3Builder(onBoardingStatus, identityBiometric);
        clientEntity.setIdCard(null);
        when(clientsV3Repository.findByIdCard(anyString()))
                .thenReturn(Option.none());
        when(clientsV3Repository.findByIdentityBiometric(any(IdentityBiometricV3.class)))
                .thenReturn(Option.of(clientEntity));
        when(databaseReference.child(anyString())).thenReturn(databaseReference);
        ClientVerificationResult payload = eventByFailedStatus(INCONSISTENCY_WITH_FACIAL_COMPARE).getPayload();
        payload.getBlacklist().setStatus(BIOMETRY_FAILED.name());

        testClass.start(payload);

        verify(databaseReference, times(1)).updateChildrenAsync(firebaseUpdateCaptor.capture());
        verify(clientsV3Repository, times(1)).save(clientEntityV3ArgumentCaptor.capture());
        ClientVerificationFirebase firebaseUpdateValue =
                (ClientVerificationFirebase) firebaseUpdateCaptor.getValue().get("clientVerification");
        ClientsV3Entity clientEntitySave = clientEntityV3ArgumentCaptor.getValue();
        assertEquals(
                "Firebase update ok", KO_IDENTITY_FRAUD.name(), firebaseUpdateValue.getVerificationResult());
        assertEquals("CheckPoint was not update", CheckPoints.ON_BOARDING.name(),
                clientEntitySave.getOnBoardingStatus().getCheckpoint());
    }

    public Event<ClientVerificationResult> eventByFailedStatus(BiometricResultCodes biometricResultCodes) {
        Event<ClientVerificationResult> event = getEvent();
        event.getPayload().getTransactionState().setId(biometricResultCodes.getCode());
        event.getPayload().getTransactionState().setStateName(biometricResultCodes.getMessage());
        return event;
    }

    private Event<ClientVerificationResult> getEvent() {
        IdDocument idDocument = Sample.idDocumentBuilder(ID_CARD, DATE_ISSUE);
        BlacklistResult blackListResult = Sample.blacklistResultBuilder(StateBlackList.NON_BLACKLISTED);
        ClientPersonalInformationResult personalInformationResult = Sample.clientPersonalInformationResultBuilder(NAME, LAST_NAME, GENDER, idDocument);
        ClientVerificationResult clientVerificationResult = Sample.clientVerificationResultBuilder(ID_TRANSACTION, personalInformationResult, blackListResult);
        Event<ClientVerificationResult> clientVerificationResultEvent = new Event<>();
        clientVerificationResultEvent.setPayload(clientVerificationResult);
        clientVerificationResultEvent.setId(UUID.randomUUID().toString());
        return clientVerificationResultEvent;
    }

}
