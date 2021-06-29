package com.lulobank.clients.v3.usecase.accountsettlement;

import com.google.firebase.database.DatabaseReference;
import com.lulobank.clients.sdk.operations.dto.onboardingclients.AccountSettlement;
import com.lulobank.clients.services.application.port.out.reporting.ReportingMessagingPort;
import com.lulobank.clients.services.application.port.out.reporting.TransactionsMessagingPort;
import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.services.exception.DigitalEvidenceException;
import com.lulobank.clients.services.outboundadapters.ClientsOutboundAdapter;
import com.lulobank.clients.services.ports.out.CustomerServiceV2;
import com.lulobank.clients.services.ports.out.DigitalEvidenceService;
import com.lulobank.clients.services.ports.out.dto.CustomerServiceResponse;
import com.lulobank.clients.services.ports.out.error.CustomerServiceError;
import com.lulobank.clients.services.ports.out.error.CustomerServiceErrorStatus;
import com.lulobank.clients.services.utils.ClientVerificationFirebase;
import com.lulobank.clients.services.utils.LuloUserTokenGenerator;
import com.lulobank.clients.services.utils.StatusClientVerificationFirebaseEnum;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.OnBoardingStatusV3;
import com.lulobank.clients.v3.adapters.port.out.savingsaccount.SavingsAccountV3Service;
import com.lulobank.clients.v3.adapters.port.out.savingsaccount.dto.SavingsAccountRequest;
import com.lulobank.clients.v3.adapters.port.out.savingsaccount.error.SavingAccountServiceErrorStatus;
import com.lulobank.clients.v3.adapters.port.out.savingsaccount.error.SavingsAccountError;
import com.lulobank.clients.v3.error.ClientsDataErrorStatus;
import com.lulobank.clients.v3.usecase.command.BiometricResponse;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static com.lulobank.clients.sdk.operations.util.CheckPoints.SAVING_ACCOUNT_CREATED;
import static com.lulobank.clients.services.Constants.ID_BIOMETRIC;
import static com.lulobank.clients.services.Constants.ID_CLIENT;
import static com.lulobank.clients.services.Sample.createSavingsResponseBuilder;
import static com.lulobank.clients.services.SamplesV3.clientEntityV3Builder;
import static com.lulobank.clients.services.SamplesV3.onBoardingStatusV3Builder;
import static com.lulobank.clients.services.utils.ProductTypeEnum.CREDIT_ACCOUNT;
import static com.lulobank.clients.services.utils.ProductTypeEnum.SAVING_ACCOUNT;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AccountSettlementUseCaseTest {

    AccountSettlementUseCase testedClass;

    @Mock
    protected ClientsOutboundAdapter clientsOutboundAdapter;
    @Mock
    protected CustomerServiceV2 customerServicev2;
    @Mock
    protected ClientsV3Repository clientsV3Repository;
    @Mock
    protected DatabaseReference databaseReference;
    @Mock
    protected LuloUserTokenGenerator luloUserTokenGenerator;
    @Mock
    protected TransactionsMessagingPort transactionsMessagingService;
    @Mock
    protected SavingsAccountV3Service savingsAccountV3Service;
    @Mock
    protected DigitalEvidenceService digitalEvidenceService;
    @Mock
    protected ReportingMessagingPort reportingMessagingPort;

    @Captor
    protected ArgumentCaptor<ClientsV3Entity> clientEntityV3ArgumentCaptor;
    @Captor
    protected ArgumentCaptor<Map<String, Object>> firebaseUpdateCaptor;

    @Captor
    protected ArgumentCaptor<String> idClient;


    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        clientsOutboundAdapter = new ClientsOutboundAdapter();
        clientsOutboundAdapter.setDatabaseReference(databaseReference);
        clientsOutboundAdapter.setLuloUserTokenGenerator(luloUserTokenGenerator);
        testedClass = new AccountSettlementUseCase(clientsOutboundAdapter,
                customerServicev2,clientsV3Repository, transactionsMessagingService,savingsAccountV3Service, digitalEvidenceService,
                reportingMessagingPort);
    }

    @Test
    public void shouldAccountSettlementWhenSuccess(){
        OnBoardingStatusV3 onBoardingStatus = onBoardingStatusV3Builder(SAVING_ACCOUNT);
        AccountSettlement accountSettlement = AccountSettlement.builder().idClient(ID_CLIENT).build();
        ClientsV3Entity client = clientEntityV3Builder(onBoardingStatus, null);
        client.setIdentityBiometricId(ID_BIOMETRIC);
        when(clientsV3Repository.findByIdClient(anyString()))
                .thenReturn(Option.of(client));
        when(customerServicev2.createUserCustomer(any(), any()))
                .thenReturn(Either.right(CustomerServiceResponse.builder().created(Boolean.TRUE).build()));
        when(clientsV3Repository.save(any())).thenReturn( Try.success(client));
        when(databaseReference.child(anyString())).thenReturn(databaseReference);
        when(savingsAccountV3Service.create(any(SavingsAccountRequest.class),any(HashMap.class)))
                .thenReturn(Either.right(createSavingsResponseBuilder()));
        when(transactionsMessagingService.checkReferralHold(any())).thenReturn( Try.run(System.out::println));
        when(digitalEvidenceService.saveDigitalEvidence(anyMap(), any(), any())).thenReturn(Try.of(() -> Boolean.TRUE));
        when(reportingMessagingPort.sendCatDocument(any())).thenReturn(Try.run(()-> System.out.println("")));

        Either<UseCaseResponseError, BiometricResponse> response =
                testedClass.execute(accountSettlement);

        verify(digitalEvidenceService, times(1)).saveDigitalEvidence(anyMap(), any(),any());
        verify(databaseReference, times(1)).updateChildrenAsync(firebaseUpdateCaptor.capture());
        verify(clientsV3Repository,times(1)).save(any());
        verify(customerServicev2,times(1)).createUserCustomer(any(),any());
        verify(savingsAccountV3Service,times(1)).create(any(),any());
        verify(transactionsMessagingService,times(1)).checkReferralHold(any());
        verify(reportingMessagingPort, times(1)).sendCatDocument(any());
        assertEquals(SAVING_ACCOUNT_CREATED.name(),client.getOnBoardingStatus().getCheckpoint());
        assertFalse(client.getIdCbs().isEmpty());
        assertEquals(StatusClientVerificationFirebaseEnum.OK.name(),
                ((ClientVerificationFirebase) firebaseUpdateCaptor.getValue().get("clientVerification")).getVerificationResult());
        assertThat(response.isRight(), is(true));
        assertThat(response.isLeft(), is(false));
        assertThat(response.isEmpty(), is(false));
        assertThat(response.get().getIdTransactionBiometric(), is(ID_BIOMETRIC));
    }

    @Test
    public void shouldReturnLeftWhenCustomerIsNotCreated() {
        OnBoardingStatusV3 onBoardingStatus = onBoardingStatusV3Builder(SAVING_ACCOUNT);
        AccountSettlement accountSettlement = AccountSettlement.builder().idClient(ID_CLIENT).build();
        ClientsV3Entity client = clientEntityV3Builder(onBoardingStatus, null);
        client.setIdentityBiometricId(ID_BIOMETRIC);
        when(clientsV3Repository.findByIdClient(anyString()))
                .thenReturn(Option.of(client));
        when(customerServicev2.createUserCustomer(any(), any()))
                .thenReturn(Either.left(CustomerServiceError.connectionError()));
        when(databaseReference.child(anyString())).thenReturn(databaseReference);
        when(savingsAccountV3Service.create(any(SavingsAccountRequest.class),any(HashMap.class)))
                .thenReturn(Either.right(createSavingsResponseBuilder()));
        when(digitalEvidenceService.saveDigitalEvidence(anyMap(), any(), any())).thenReturn(Try.of(() -> Boolean.TRUE));
        when(reportingMessagingPort.sendCatDocument(any())).thenReturn(Try.run(()-> System.out.println("")));

        Either<UseCaseResponseError, BiometricResponse> response =
                testedClass.execute(accountSettlement);

        verify(digitalEvidenceService, times(0)).saveDigitalEvidence(anyMap(), any(),any());
        verify(databaseReference, times(1)).updateChildrenAsync(firebaseUpdateCaptor.capture());
        verify(clientsV3Repository,times(0)).save(any());
        verify(customerServicev2,times(1)).createUserCustomer(any(),any());
        verify(savingsAccountV3Service,times(0)).create(any(),any());
        verify(transactionsMessagingService,times(0)).checkReferralHold(any());
        verify(reportingMessagingPort, times(0)).sendCatDocument(any());
        assertNotEquals(SAVING_ACCOUNT_CREATED.name(),client.getOnBoardingStatus().getCheckpoint());
        assertNull(client.getIdCbs());
        assertEquals(StatusClientVerificationFirebaseEnum.KO_ZENDESK_USER_CREATION.name(),
                ((ClientVerificationFirebase) firebaseUpdateCaptor.getAllValues().get(0).get("clientVerification")).getVerificationResult());
        assertThat(response.isRight(), is(false));
        assertThat(response.isLeft(), is(true));
        assertThat(response.getLeft().getDetail(), is(CustomerServiceErrorStatus.DEFAULT_DETAIL));
    }

    @Test
    public void ShouldReturnLeftWhenCreateSavingAccountFail() {
        OnBoardingStatusV3 onBoardingStatus = onBoardingStatusV3Builder(SAVING_ACCOUNT);
        AccountSettlement accountSettlement = AccountSettlement.builder().idClient(ID_CLIENT).build();
        ClientsV3Entity client = clientEntityV3Builder(onBoardingStatus, null);
        client.setIdentityBiometricId(ID_BIOMETRIC);
        when(clientsV3Repository.findByIdClient(anyString()))
                .thenReturn(Option.of(client));
        when(customerServicev2.createUserCustomer(any(), any()))
                .thenReturn(Either.right(CustomerServiceResponse.builder().created(Boolean.TRUE).build()));
        when(databaseReference.child(anyString())).thenReturn(databaseReference);
        when(savingsAccountV3Service.create(any(SavingsAccountRequest.class),any(HashMap.class)))
                .thenReturn(Either.left(SavingsAccountError.connectionError()));
        when(digitalEvidenceService.saveDigitalEvidence(anyMap(), any(), any())).thenReturn(Try.of(() -> Boolean.TRUE));
        when(reportingMessagingPort.sendCatDocument(any())).thenReturn(Try.run(()-> System.out.println("")));

        Either<UseCaseResponseError, BiometricResponse> response =
                testedClass.execute(accountSettlement);

        verify(digitalEvidenceService, times(0)).saveDigitalEvidence(anyMap(), any(),any());
        verify(databaseReference, times(1)).updateChildrenAsync(firebaseUpdateCaptor.capture());
        verify(clientsV3Repository,times(0)).save(any());
        verify(customerServicev2,times(1)).createUserCustomer(any(),any());
        verify(savingsAccountV3Service,times(1)).create(any(),any());
        verify(transactionsMessagingService,times(0)).checkReferralHold(any());
        verify(reportingMessagingPort, times(0)).sendCatDocument(any());
        assertNotEquals(SAVING_ACCOUNT_CREATED.name(),client.getOnBoardingStatus().getCheckpoint());
        assertNull(client.getIdCbs());
        assertEquals(StatusClientVerificationFirebaseEnum.KO_SAVINGS_ACCOUNT_CREATION.name(),
                ((ClientVerificationFirebase) firebaseUpdateCaptor.getAllValues().get(0).get("clientVerification")).getVerificationResult());
        assertThat(response.isRight(), is(false));
        assertThat(response.getLeft().getDetail(), is(SavingAccountServiceErrorStatus.DEFAULT_DETAIL));
    }

    @Test
    public void ShouldReturnLeftWhenCreateSavingAccountCheckReferralHoldFail() {
        OnBoardingStatusV3 onBoardingStatus = onBoardingStatusV3Builder(SAVING_ACCOUNT);
        AccountSettlement accountSettlement = AccountSettlement.builder().idClient(ID_CLIENT).build();
        ClientsV3Entity client = clientEntityV3Builder(onBoardingStatus, null);
        client.setIdentityBiometricId(ID_BIOMETRIC);
        when(clientsV3Repository.findByIdClient(anyString()))
                .thenReturn(Option.of(client));
        when(customerServicev2.createUserCustomer(any(), any()))
                .thenReturn(Either.right(CustomerServiceResponse.builder().created(Boolean.TRUE).build()));
        when(databaseReference.child(anyString())).thenReturn(databaseReference);
        when(savingsAccountV3Service.create(any(SavingsAccountRequest.class),any(HashMap.class)))
                .thenReturn(Either.right(createSavingsResponseBuilder()));
        when(digitalEvidenceService.saveDigitalEvidence(anyMap(), any(), any())).thenReturn(Try.of(() -> Boolean.TRUE));
        when(reportingMessagingPort.sendCatDocument(any())).thenReturn(Try.run(()-> System.out.println("")));

        when(transactionsMessagingService.checkReferralHold(any()))
                .thenReturn(Try.failure(new RuntimeException()));

        Either<UseCaseResponseError, BiometricResponse> response =
                testedClass.execute(accountSettlement);

        verify(digitalEvidenceService, times(0)).saveDigitalEvidence(anyMap(), any(),any());
        verify(databaseReference, times(1)).updateChildrenAsync(firebaseUpdateCaptor.capture());
        verify(clientsV3Repository,times(0)).save(any());
        verify(customerServicev2,times(1)).createUserCustomer(any(),any());
        verify(savingsAccountV3Service,times(1)).create(any(),any());
        verify(transactionsMessagingService,times(1)).checkReferralHold(any());
        verify(reportingMessagingPort, times(0)).sendCatDocument(any());
        assertNotEquals(SAVING_ACCOUNT_CREATED.name(),client.getOnBoardingStatus().getCheckpoint());
        assertNotNull(client.getIdCbs());
        assertEquals(StatusClientVerificationFirebaseEnum.KO_SAVINGS_ACCOUNT_CREATION.name(),
                ((ClientVerificationFirebase) firebaseUpdateCaptor.getAllValues().get(0).get("clientVerification")).getVerificationResult());
        assertThat(response.isRight(), is(false));
        assertThat(response.getLeft().getDetail(), is(SavingAccountServiceErrorStatus.DEFAULT_DETAIL));
    }

    @Test
    public void ShouldReturnLeftWhenCreateSavingAccountFailBecauseClientHasNotProduct() {
        OnBoardingStatusV3 onBoardingStatus = onBoardingStatusV3Builder(SAVING_ACCOUNT);
        AccountSettlement accountSettlement = AccountSettlement.builder().idClient(ID_CLIENT).build();
        ClientsV3Entity client = clientEntityV3Builder(onBoardingStatus, null);
        client.getOnBoardingStatus().setProductSelected(CREDIT_ACCOUNT.name());
        client.setIdentityBiometricId(ID_BIOMETRIC);
        when(clientsV3Repository.findByIdClient(anyString()))
                .thenReturn(Option.of(client));
        when(customerServicev2.createUserCustomer(any(), any()))
                .thenReturn(Either.right(CustomerServiceResponse.builder().created(Boolean.TRUE).build()));
        when(databaseReference.child(anyString())).thenReturn(databaseReference);
        when(savingsAccountV3Service.create(any(SavingsAccountRequest.class),any(HashMap.class)))
                .thenReturn(Either.left(SavingsAccountError.connectionError()));
        when(digitalEvidenceService.saveDigitalEvidence(anyMap(), any(), any())).thenReturn(Try.of(() -> Boolean.TRUE));
        when(reportingMessagingPort.sendCatDocument(any())).thenReturn(Try.run(()-> System.out.println("")));

        Either<UseCaseResponseError, BiometricResponse> response =
                testedClass.execute(accountSettlement);

        verify(digitalEvidenceService, times(0)).saveDigitalEvidence(anyMap(), any(),any());
        verify(databaseReference, times(1)).updateChildrenAsync(firebaseUpdateCaptor.capture());
        verify(clientsV3Repository,times(0)).save(any());
        verify(customerServicev2,times(1)).createUserCustomer(any(),any());
        verify(savingsAccountV3Service,times(0)).create(any(),any());
        verify(transactionsMessagingService,times(0)).checkReferralHold(any());
        verify(reportingMessagingPort, times(0)).sendCatDocument(any());
        assertNotEquals(SAVING_ACCOUNT_CREATED.name(),client.getOnBoardingStatus().getCheckpoint());
        assertNull(client.getIdCbs());
        assertEquals(StatusClientVerificationFirebaseEnum.KO_SAVINGS_ACCOUNT_CREATION.name(),
                ((ClientVerificationFirebase) firebaseUpdateCaptor.getAllValues().get(0).get("clientVerification")).getVerificationResult());
        assertThat(response.isRight(), is(false));
        assertThat(response.getLeft().getDetail(), is(SavingAccountServiceErrorStatus.DEFAULT_DETAIL));
    }

    @Test
    public void shouldReturnLeftWhenDigitalEvidenceIsNotCreated() {
        OnBoardingStatusV3 onBoardingStatus = onBoardingStatusV3Builder(SAVING_ACCOUNT);
        AccountSettlement accountSettlement = AccountSettlement.builder().idClient(ID_CLIENT).build();
        ClientsV3Entity client = clientEntityV3Builder(onBoardingStatus, null);
        client.setIdentityBiometricId(ID_BIOMETRIC);
        when(clientsV3Repository.findByIdClient(anyString()))
                .thenReturn(Option.of(client));
        when(customerServicev2.createUserCustomer(any(), any()))
                .thenReturn(Either.right(CustomerServiceResponse.builder().created(Boolean.TRUE).build()));
        when(digitalEvidenceService.saveDigitalEvidence(any(), any(), any()))
                .thenReturn(Try.failure(new DigitalEvidenceException("Error")));
        when(databaseReference.child(anyString())).thenReturn(databaseReference);
        when(savingsAccountV3Service.create(any(SavingsAccountRequest.class),any(HashMap.class)))
                .thenReturn(Either.right(createSavingsResponseBuilder()));
        when(transactionsMessagingService.checkReferralHold(any())).thenReturn( Try.run(System.out::println));
        when(reportingMessagingPort.sendCatDocument(any())).thenReturn(Try.run(()-> System.out.println("")));

        Either<UseCaseResponseError, BiometricResponse> response =
                testedClass.execute(accountSettlement);

        verify(databaseReference, times(1)).updateChildrenAsync(firebaseUpdateCaptor.capture());
        verify(clientsV3Repository,times(0)).save(any());
        verify(customerServicev2,times(1)).createUserCustomer(any(),any());
        verify(digitalEvidenceService,times(1)).saveDigitalEvidence(any(),any(),any());
        verify(reportingMessagingPort, times(0)).sendCatDocument(any());
        assertNotEquals(SAVING_ACCOUNT_CREATED.name(),client.getOnBoardingStatus().getCheckpoint());
        assertEquals(StatusClientVerificationFirebaseEnum.KO_DIGITAL_EVIDENCE.name(),
                ((ClientVerificationFirebase) firebaseUpdateCaptor.getAllValues().get(0).get("clientVerification")).getVerificationResult());
        assertThat(response.isRight(), is(false));
        assertThat(response.isLeft(), is(true));
        assertThat(response.getLeft().getDetail(), is(ClientsDataErrorStatus.DEFAULT_DETAIL));
    }


    @Test
    public void shouldAccountSettlementWhenClientNotFound(){
        OnBoardingStatusV3 onBoardingStatus = onBoardingStatusV3Builder(SAVING_ACCOUNT);
        AccountSettlement accountSettlement = AccountSettlement.builder().idClient(ID_CLIENT).build();
        ClientsV3Entity client = clientEntityV3Builder(onBoardingStatus, null);
        client.setIdentityBiometricId(ID_BIOMETRIC);
        when(clientsV3Repository.findByIdClient(anyString()))
                .thenReturn(Option.of(null));
        when(customerServicev2.createUserCustomer(any(), any()))
                .thenReturn(Either.right(CustomerServiceResponse.builder().created(Boolean.TRUE).build()));
        when(databaseReference.child(anyString())).thenReturn(databaseReference);
        when(savingsAccountV3Service.create(any(SavingsAccountRequest.class),any(HashMap.class)))
                .thenReturn(Either.right(createSavingsResponseBuilder()));
        when(digitalEvidenceService.saveDigitalEvidence(anyMap(), any(), any())).thenReturn(Try.of(() -> Boolean.TRUE));
        when(reportingMessagingPort.sendCatDocument(any())).thenReturn(Try.run(()-> System.out.println("")));

        Either<UseCaseResponseError, BiometricResponse> response =
                testedClass.execute(accountSettlement);

        verify(digitalEvidenceService, times(0)).saveDigitalEvidence(anyMap(), any(),any());
        verify(databaseReference, times(0)).updateChildrenAsync(firebaseUpdateCaptor.capture());
        verify(clientsV3Repository,times(0)).save(any());
        verify(customerServicev2,times(0)).createUserCustomer(any(),any());
        verify(savingsAccountV3Service,times(0)).create(any(),any());
        verify(transactionsMessagingService,times(0)).checkReferralHold(any());
        verify(reportingMessagingPort, times(0)).sendCatDocument(any());
        assertNotEquals(SAVING_ACCOUNT_CREATED.name(),client.getOnBoardingStatus().getCheckpoint());
        assertNull(client.getIdCbs());
        assertThat(response.isRight(), is(false));
        assertThat(response.isLeft(), is(true));
        assertThat(response.isEmpty(), is(true));
        UseCaseResponseError useCaseResponseError = response.getLeft();
        assertThat(useCaseResponseError.getBusinessCode(), is("CLI_101"));
        assertThat(useCaseResponseError.getDetail(), is("D"));
        assertThat(useCaseResponseError.getProviderCode(), is("404"));
    }

}
