package com.lulobank.clients.services.features.clientverificationresult;

import com.google.firebase.database.DatabaseReference;
import com.lulobank.clients.sdk.operations.util.CheckPoints;
import com.lulobank.clients.services.Sample;
import com.lulobank.clients.services.application.port.out.clientnotify.ClientNotifyService;
import com.lulobank.clients.services.domain.RiskLevelBlackList;
import com.lulobank.clients.services.domain.StateBlackList;
import com.lulobank.clients.services.events.ClientVerificationResult;
import com.lulobank.clients.services.outboundadapters.ClientsOutboundAdapter;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.ports.out.CustomerService;
import com.lulobank.clients.services.utils.ClientVerificationFirebase;
import com.lulobank.clients.services.utils.IdentityBiometricStatus;
import com.lulobank.clients.services.utils.LuloUserTokenGenerator;
import com.lulobank.clients.services.utils.StatusClientVerificationFirebaseEnum;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.IdentityBiometricV3;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.OnBoardingStatusV3;
import com.lulobank.core.events.Event;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

import static com.lulobank.clients.services.Constants.ID_CARD;
import static com.lulobank.clients.services.Constants.ID_TRANSACTION;
import static com.lulobank.clients.services.SamplesV3.clientEntityV3Builder;
import static com.lulobank.clients.services.SamplesV3.identityBiometricV3Builder;
import static com.lulobank.clients.services.SamplesV3.onBoardingStatusV3Builder;
import static com.lulobank.clients.services.domain.StateBlackList.BLACKLISTED;
import static com.lulobank.clients.services.domain.StateBlackList.FAILED;
import static com.lulobank.clients.services.utils.BiometricResultCodes.SUCCESSFUL;
import static com.lulobank.clients.services.utils.IdentityBiometricStatus.IN_PROGRESS;
import static com.lulobank.clients.services.utils.ProductTypeEnum.SAVING_ACCOUNT;
import static com.lulobank.clients.services.utils.StatusClientVerificationFirebaseEnum.BLACKLIST_FAILED;
import static com.lulobank.clients.services.utils.StatusClientVerificationFirebaseEnum.KO_BLACKLIST_HIGH_RISK;
import static com.lulobank.clients.services.utils.StatusClientVerificationFirebaseEnum.KO_BLACKLIST_MEDIUM_RISK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BlacklistedProcessServiceTest {

    private BlacklistedProcessService testClass;

    @Mock
    protected ClientsV3Repository clientsV3Repository;
    @Mock
    protected DatabaseReference databaseReference;
    @Mock
    protected CustomerService customerService;
    @Captor
    protected ArgumentCaptor<Map<String, Object>> firebaseUpdateCaptor;
    @Captor
    protected ArgumentCaptor<ClientsV3Entity> clientEntityV3ArgumentCaptor;
    @Mock
    protected ClientNotifyService clientNotifyService;
    @Mock
    protected ClientsOutboundAdapter clientsOutboundAdapter;
    @Mock
    protected LuloUserTokenGenerator luloUserTokenGenerator;

    @Captor private ArgumentCaptor<Boolean> booleanArgumentCaptor;

    private final Try<Void> doNothingMethod = Try.run(() -> {});

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        clientsOutboundAdapter = new ClientsOutboundAdapter();
        clientsOutboundAdapter.setDatabaseReference(databaseReference);
        clientsOutboundAdapter.setLuloUserTokenGenerator(luloUserTokenGenerator);
        testClass = new BlacklistedProcessService(clientsOutboundAdapter,clientNotifyService,
                clientsV3Repository, customerService);
    }

    @Test
    public void blacklistedEventOk() {
        OnBoardingStatusV3 onBoardingStatus = onBoardingStatusV3Builder(SAVING_ACCOUNT);
        IdentityBiometricV3 identityBiometric = identityBiometricV3Builder(ID_TRANSACTION, IN_PROGRESS.name());
        ClientsV3Entity clientEntity = clientEntityV3Builder(onBoardingStatus, identityBiometric);
        when(clientsV3Repository.findByIdentityBiometric(any(IdentityBiometricV3.class)))
                .thenReturn(Option.of(clientEntity));
        when(databaseReference.child(anyString())).thenReturn(databaseReference);
        when(clientsV3Repository.findByIdCard(anyString()))
                .thenReturn(Option.none());
        when(customerService.createUserCustomer(any(), any())).thenReturn(Try.of(() -> Boolean.TRUE));
        Event<ClientVerificationResult> event = Sample.getEvent();
        event.getPayload().getBlacklist().setStatus(BLACKLISTED.name());
        event.getPayload().getBlacklist().setResultRiskLevel(RiskLevelBlackList.HIGH_RISK.getLevel());
        event.getPayload().getBlacklist().setReportDate(LocalDateTime.now().toString());
        testClass.rejectClient(clientEntity, event.getPayload());
        verify(databaseReference, times(1)).updateChildrenAsync(firebaseUpdateCaptor.capture());
        verify(clientsV3Repository, times(1)).save(clientEntityV3ArgumentCaptor.capture());
        assertEquals(StatusClientVerificationFirebaseEnum.KO_BLACKLIST_HIGH_RISK.name(),
                ((ClientVerificationFirebase) firebaseUpdateCaptor.getValue().get("clientVerification")).getVerificationResult());
        ClientsV3Entity clientEntitySave = clientEntityV3ArgumentCaptor.getValue();
        assertClientEntityBlackListed(clientEntitySave);
    }

    @Test
    public void blacklistFailed() {
        OnBoardingStatusV3 onBoardingStatus = onBoardingStatusV3Builder(SAVING_ACCOUNT);
        IdentityBiometricV3 identityBiometric = identityBiometricV3Builder(ID_TRANSACTION, IN_PROGRESS.name());
        ClientsV3Entity clientEntity = clientEntityV3Builder(onBoardingStatus, identityBiometric);
        when(databaseReference.child(anyString())).thenReturn(databaseReference);
        when(customerService.createUserCustomer(any(), any())).thenReturn(Try.of(() -> Boolean.TRUE));
        Event<ClientVerificationResult> event = Sample.getEvent();
        event.getPayload().getBlacklist().setStatus(FAILED.name());
        testClass.rejectClient(clientEntity, event.getPayload());
        verify(databaseReference, times(1)).updateChildrenAsync(firebaseUpdateCaptor.capture());
        ClientVerificationFirebase firebaseUpdateValue =
                (ClientVerificationFirebase) firebaseUpdateCaptor.getValue().get("clientVerification");
        assertEquals("Firebase update ok", BLACKLIST_FAILED.name(), firebaseUpdateValue.getVerificationResult());
    }


     @Test
    public void clientBlacklistedRiskLevel3() {
        OnBoardingStatusV3 onBoardingStatus = onBoardingStatusV3Builder(SAVING_ACCOUNT);
        IdentityBiometricV3 identityBiometric = identityBiometricV3Builder(ID_TRANSACTION, IN_PROGRESS.name());
        ClientsV3Entity clientEntity = clientEntityV3Builder(onBoardingStatus, identityBiometric);
        when(customerService.createUserCustomer(any(), any())).thenReturn(Try.of(() -> Boolean.TRUE));
        when(databaseReference.child(anyString())).thenReturn(databaseReference);
        when(clientNotifyService.sendBlacklistNotification(any())).thenReturn(doNothingMethod);
        testClass.rejectClient(clientEntity, eventByFailedBlackList().getPayload());
        verify(databaseReference, times(1)).updateChildrenAsync(firebaseUpdateCaptor.capture());
        verify(clientsV3Repository, times(1)).save(clientEntityV3ArgumentCaptor.capture());
        ClientVerificationFirebase firebaseUpdateValue =
                (ClientVerificationFirebase) firebaseUpdateCaptor.getValue().get("clientVerification");
        ClientsV3Entity clientEntitySave = clientEntityV3ArgumentCaptor.getValue();
        assertEquals(
                "Firebase update ok", KO_BLACKLIST_HIGH_RISK.name(), firebaseUpdateValue.getVerificationResult());
        assertEquals("CheckPoint was not update", CheckPoints.BLACKLISTED.name(),
                clientEntitySave.getOnBoardingStatus().getCheckpoint());
        verify(clientNotifyService, times(1)).sendBlacklistNotification(any());

    }


    @Test
    public void clientBlacklistedRiskLevel2() {
        OnBoardingStatusV3 onBoardingStatus = onBoardingStatusV3Builder(SAVING_ACCOUNT);
        IdentityBiometricV3 identityBiometric = identityBiometricV3Builder(ID_TRANSACTION, IN_PROGRESS.name());
        ClientsV3Entity clientEntity = clientEntityV3Builder(onBoardingStatus, identityBiometric);
        when(customerService.createUserCustomer(any(), any())).thenReturn(Try.of(() -> Boolean.TRUE));
        when(databaseReference.child(anyString())).thenReturn(databaseReference);
        when(clientNotifyService.sendBlacklistNotification(any())).thenReturn(doNothingMethod);

        ClientVerificationResult event = eventByFailedBlackList().getPayload();
        event.getBlacklist().setResultRiskLevel(RiskLevelBlackList.MID_RISK.getLevel());
        testClass.rejectClient(clientEntity, event);
        verify(databaseReference, times(1)).updateChildrenAsync(firebaseUpdateCaptor.capture());
        verify(clientsV3Repository, times(1)).save(clientEntityV3ArgumentCaptor.capture());
        ClientVerificationFirebase firebaseUpdateValue =
                (ClientVerificationFirebase) firebaseUpdateCaptor.getValue().get("clientVerification");
        ClientsV3Entity clientEntitySave = clientEntityV3ArgumentCaptor.getValue();
        assertEquals(
                "Firebase update ok", KO_BLACKLIST_MEDIUM_RISK.name(), firebaseUpdateValue.getVerificationResult());
        assertEquals("CheckPoint was not update", CheckPoints.BLACKLISTED.name(),
                clientEntitySave.getOnBoardingStatus().getCheckpoint());
        verify(clientNotifyService, times(1)).sendBlacklistNotification(any());

    }

    public Event<ClientVerificationResult> eventByFailedBlackList() {
        Event<ClientVerificationResult> event = Sample.getEvent();
        event.getPayload().getBlacklist().setStatus(StateBlackList.BLACKLISTED.name());
        event.getPayload().getBlacklist().setResultRiskLevel(RiskLevelBlackList.HIGH_RISK.getLevel());
        event.getPayload().getBlacklist().setReportDate(LocalDateTime.now().toString());
        return event;
    }

        private void assertClientEntityBlackListed(ClientsV3Entity clientEntitySave) {
        assertEquals("Identity Status was save Right",
                IN_PROGRESS.name(),
                clientEntitySave.getIdentityBiometric().getStatus());
        assertEquals("IdCard was save Right",
                ID_CARD,
                clientEntitySave.getIdCard());
    }

        private void assertClientEntity(ClientEntity clientEntitySave) {
        assertEquals("Identity Status was save Right",
                IdentityBiometricStatus.FINISHED.name(),
                clientEntitySave.getIdentityBiometric().getStatus());
        assertEquals("Transaction state was  save right",
                SUCCESSFUL.getCode(),
                clientEntitySave.getIdentityBiometric().getTransactionState().getId());
        assertEquals("IdCard was save Right",
                ID_CARD,
                clientEntitySave.getIdCard());
        assertTrue(
                "Additional information is not null",
                Objects.nonNull(clientEntitySave.getAdditionalPersonalInformation()));
    }

}
