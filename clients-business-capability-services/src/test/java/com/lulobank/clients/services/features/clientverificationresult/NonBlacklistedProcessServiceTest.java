package com.lulobank.clients.services.features.clientverificationresult;

import com.google.firebase.database.DatabaseReference;
import com.lulobank.clients.sdk.operations.util.CheckPoints;
import com.lulobank.clients.services.Sample;
import com.lulobank.clients.services.domain.StateBlackList;
import com.lulobank.clients.services.outboundadapters.ClientsOutboundAdapter;
import com.lulobank.clients.services.utils.ClientVerificationFirebase;
import com.lulobank.clients.services.utils.IdentityBiometricStatus;
import com.lulobank.clients.services.utils.LuloUserTokenGenerator;
import com.lulobank.clients.services.utils.StatusClientVerificationFirebaseEnum;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.IdentityBiometricV3;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.OnBoardingStatusV3;
import com.lulobank.clients.v3.adapters.port.out.riskengine.RiskEngineService;
import com.lulobank.savingsaccounts.sdk.operations.ISavingsAccount;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;
import java.util.Objects;

import static com.lulobank.clients.services.Constants.ID_CARD;
import static com.lulobank.clients.services.Constants.ID_TRANSACTION;
import static com.lulobank.clients.services.SamplesV3.clientEntityV3Builder;
import static com.lulobank.clients.services.SamplesV3.identityBiometricV3Builder;
import static com.lulobank.clients.services.SamplesV3.onBoardingStatusV3Builder;
import static com.lulobank.clients.services.utils.BiometricResultCodes.SUCCESSFUL;
import static com.lulobank.clients.services.utils.IdentityBiometricStatus.IN_PROGRESS;
import static com.lulobank.clients.services.utils.ProductTypeEnum.SAVING_ACCOUNT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

public class NonBlacklistedProcessServiceTest {

    private NonBlacklistedProcessService testClass;

    @Mock
    protected ISavingsAccount savingsAccount;
    @Mock
    protected DatabaseReference databaseReference;
    @Captor
    protected ArgumentCaptor<ClientsV3Entity> clientEntityV3ArgumentCaptor;
    @Captor
    protected ArgumentCaptor<Map<String, Object>> firebaseUpdateCaptor;
    @Mock
    protected LuloUserTokenGenerator luloUserTokenGenerator;
    @Mock
    protected ClientsOutboundAdapter clientsOutboundAdapter;
    @Mock
    protected ClientsV3Repository clientsV3Repository;
    @Mock
    private RiskEngineService riskEngineService;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        clientsOutboundAdapter = new ClientsOutboundAdapter();
        clientsOutboundAdapter.setDatabaseReference(databaseReference);
        clientsOutboundAdapter.setLuloUserTokenGenerator(luloUserTokenGenerator);
        clientsOutboundAdapter.setSavingsAccount(savingsAccount);
        testClass = new NonBlacklistedProcessService(clientsOutboundAdapter, clientsV3Repository, riskEngineService);
    }

    @Test
    public void nonBlacklistedProcessOk(){
        OnBoardingStatusV3 onBoardingStatus = onBoardingStatusV3Builder(SAVING_ACCOUNT);
        IdentityBiometricV3 identityBiometric = identityBiometricV3Builder(ID_TRANSACTION, IN_PROGRESS.name());
        ClientsV3Entity clientEntity = clientEntityV3Builder(onBoardingStatus, identityBiometric);
        when(databaseReference.child(anyString())).thenReturn(databaseReference);
        when(clientsV3Repository.save(clientEntityV3ArgumentCaptor.capture()))
                .thenReturn(Try.of(() -> clientEntity));
        when(riskEngineService.sendValidateClientWLMessage(any())).thenReturn(Try.run(() -> System.out.println("success process")));
        testClass.emitEvent(clientEntity, Sample.getEvent().getPayload());
        verify(databaseReference, times(1)).updateChildrenAsync(firebaseUpdateCaptor.capture());
        assertEquals(StatusClientVerificationFirebaseEnum.START_PEP_VALIDATION.name(),
                ((ClientVerificationFirebase) firebaseUpdateCaptor.getValue().get("clientVerification")).getVerificationResult());
        assertEquals(CheckPoints.BLACKLIST_FINISHED.name(), clientEntityV3ArgumentCaptor.getValue().getOnBoardingStatus().getCheckpoint());
        assertEquals(StateBlackList.NON_BLACKLISTED.name(), clientEntityV3ArgumentCaptor.getValue().getBlackListState());
    }



    private void assertClientEntity(ClientsV3Entity clientEntitySave) {
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
