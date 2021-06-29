package com.lulobank.clients.services.inboundadapters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.DatabaseReference;
import com.lulobank.clients.services.events.BlacklistResult;
import com.lulobank.clients.services.events.ClientPersonalInformationResult;
import com.lulobank.clients.services.events.ClientVerificationResult;
import com.lulobank.clients.services.events.EmailVerified;
import com.lulobank.clients.services.events.IdDocument;
import com.lulobank.clients.services.events.TransactionState;
import com.lulobank.clients.services.events.UpdateClientAddressEvent;
import com.lulobank.clients.services.features.RetriesOption;
import com.lulobank.clients.services.features.onboardingclients.action.MessageToNotifySQSClients;
import com.lulobank.clients.services.features.onboardingclients.action.MessageToNotifySQSRiskEngine;
import com.lulobank.clients.services.features.profile.UpdateClientAddressEventUseCase;
import com.lulobank.clients.services.features.profile.UpdateClientAddressService;
import com.lulobank.clients.services.features.profile.mapper.ClientsEntityV3Mapper;
import com.lulobank.clients.services.features.riskscoreresponse.RiskScoringResultFromHomeEventHandler;
import com.lulobank.clients.services.features.riskscoreresponse.RiskScoringResultFromOnbordingEventHandler;
import com.lulobank.clients.services.outboundadapters.ClientsOutboundAdapter;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.model.OnBoardingStatus;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.clients.services.ports.out.corebanking.ClientInfoCoreBankingPort;
import com.lulobank.clients.services.ports.repository.ClientsRepositoryV2;
import com.lulobank.clients.services.utils.LuloUserTokenGenerator;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.core.events.Event;
import com.lulobank.core.utils.EventUtils;
import com.lulobank.credits.sdk.operations.InitialOffersOperations;
import com.lulobank.savingsaccounts.sdk.operations.ISavingsAccount;
import io.vavr.control.Option;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static com.lulobank.clients.sdk.operations.util.CheckPoints.FINISH_ON_BOARDING;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SqsClientListenerAdapterTest {
  public static final String ID_CARD = "1067843466";
  public static final String ACCESS_TOKEN = "132wasdasdfsa";
  private static final String ID_CLIENT = "1106bc49-4a0f-4f52-86ca-1994bb3c26d9";
  private static final String ADDRESS = "address_test";
  private static final String ADDRESS_PREFIX = "prefix_test";
  private static final String ADDRESS_COMPLEMENT = "complement_test";
  private static final String DEPARTMENT = "department_test";
  private static final String DEPARTMENT_ID = "1";
  private static final String CITY = "city_test";
  private static final String CITY_ID = "1";
  private static final String CODE = "ADL";
  EmailVerified epayload;
  ClientEntity clientEntity;
  @Mock private ClientsRepository clientsRepository;
  @Mock private ClientsRepositoryV2 clientsRepositoryV2;
  @Mock private ClientsV3Repository clientsRepositoryV3;

  @Mock private ClientInfoCoreBankingPort clientInfoCoreBankingPort;
  private SqsClientListenerAdapter testedClass;
  private Event<EmailVerified> eventEmailVerified;
  private Event<UpdateClientAddressEvent> updateClientAddressEvent;
  @Mock private InitialOffersOperations initialOffersOperations;
  private Event<ClientVerificationResult> clientVerificationResultEvent;
  private RiskScoringResultFromHomeEventHandler riskScoringResultFromHomeEventHandler;
  private RiskScoringResultFromOnbordingEventHandler riskScoringResultFromOnbordingEventHandler;
  private UpdateClientAddressEventUseCase updateClientAddressEventUseCase;
  private UpdateClientAddressService updateClientAddressService;

  @Mock private ISavingsAccount savingsAccount;
  @Mock private MessageToNotifySQSRiskEngine messageToNotifySQSRiskEngine;
  @Mock private MessageToNotifySQSClients messageToNotifySQSClients;
  @Mock private LuloUserTokenGenerator luloUserTokenGenerator;
  @Mock private DatabaseReference databaseReference;
  @InjectMocks private ClientsOutboundAdapter clientsOutboundAdapter;
  @Mock private RetriesOption retriesOption;
  @Captor private ArgumentCaptor<Map> firebaseParametersCaptor;
  @Captor
  private ArgumentCaptor<ClientsV3Entity> clientsV3EntityCaptor;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    String UUDIStr = "aa9504b5-0485-4fc8-aacc-8d9f64092664";
    updateClientAddressService = new UpdateClientAddressService(clientsRepositoryV3,null,clientInfoCoreBankingPort);

    updateClientAddressEventUseCase = new UpdateClientAddressEventUseCase(updateClientAddressService,clientsRepositoryV3);
    testedClass = new SqsClientListenerAdapter(clientsRepository, null, null, null,updateClientAddressEventUseCase);
    epayload = new EmailVerified();
    epayload.setIdClient(UUDIStr);
    eventEmailVerified = new Event<>();
    clientEntity = new ClientEntity();
    clientEntity.setIdClient(UUDIStr);
    clientEntity.setIdCard(ID_CARD);
    clientEntity.setOnBoardingStatus(new OnBoardingStatus());
    BlacklistResult blacklistResult = new BlacklistResult();
    blacklistResult.setReportDate("2015-08-04T10:11:30");
    blacklistResult.setStatus("NON_BLACKLISTED");
    IdDocument idDocument = new IdDocument();
    idDocument.setDocumentType("CC");
    idDocument.setExpeditionDate("1999-05-04T00:00:00");
    idDocument.setIdCard(ID_CARD);
    ClientPersonalInformationResult clientPersonalInformationResult =
        new ClientPersonalInformationResult();
    clientPersonalInformationResult.setBirthDate("1981-02-04T00:00:00");
    clientPersonalInformationResult.setIdDocument(idDocument);
    clientPersonalInformationResult.setLastName("LAST_NAME_TEST");
    clientPersonalInformationResult.setName("TEST");
    ClientVerificationResult clientVerificationResult = new ClientVerificationResult();
    clientVerificationResult.setStatus("OK");
    clientVerificationResult.setBlacklist(blacklistResult);
    clientVerificationResult.setClientPersonalInformation(clientPersonalInformationResult);
    clientVerificationResult.setIdTransactionBiometric("22");
    TransactionState transactionState = new TransactionState();
    transactionState.setId(2);
    transactionState.setStateName("ok");
    clientVerificationResult.setTransactionState(transactionState);
    clientVerificationResultEvent = new EventUtils().getEvent(clientVerificationResult);

    UpdateClientAddressEvent updateClientAddress = new UpdateClientAddressEvent();
    updateClientAddress.setIdClient(ID_CLIENT);
    updateClientAddress.setAddress(ADDRESS);
    updateClientAddress.setAddressPrefix(ADDRESS_PREFIX);
    updateClientAddress.setAddressComplement(ADDRESS_COMPLEMENT);
    updateClientAddress.setCity(CITY);
    updateClientAddress.setCityId(CITY_ID);
    updateClientAddress.setDepartment(DEPARTMENT);
    updateClientAddress.setDepartmentId(DEPARTMENT_ID);
    updateClientAddress.setCheckpoint(FINISH_ON_BOARDING.name());
    updateClientAddress.setCode(CODE);
    updateClientAddressEvent = new EventUtils().getEvent(updateClientAddress);
  }

  @Test
  public void shouldNotUpdateAddressSinceClientNotFound() throws JsonProcessingException {
    when(clientsRepositoryV3.findByIdClient(any(String.class)))
            .thenReturn(Option.none());
    ObjectMapper objectMapper = new ObjectMapper();
    testedClass.getMessage(new HashMap<>(), objectMapper.writeValueAsString(updateClientAddressEvent));
    verify(clientsRepositoryV3,times(0)).save(any());
  }

  @Test
  public void shouldUpdateAddress() throws JsonProcessingException {
    when(clientsRepositoryV3.findByIdClient(any(String.class)))
            .thenReturn(Option.of(ClientsEntityV3Mapper.INSTANCE.toClientsV3Entity(clientEntity)));
    ObjectMapper objectMapper = new ObjectMapper();
    testedClass.getMessage(new HashMap<>(), objectMapper.writeValueAsString(updateClientAddressEvent));
    verify(clientsRepositoryV3,times(2)).save(clientsV3EntityCaptor.capture());
    assertThat(clientsV3EntityCaptor.getValue().getCode(),is(CODE));
  }


  @Test
  public void shouldUPDATEUserEmailVerified() throws JsonProcessingException {
    eventEmailVerified.setId(String.valueOf(System.currentTimeMillis()));
    eventEmailVerified.setEventType(EmailVerified.class.getSimpleName());
    eventEmailVerified.setPayload(epayload);
    ObjectMapper objectMapper = new ObjectMapper();
    String eventRequest = objectMapper.writeValueAsString(eventEmailVerified);
    when(clientsRepository.findByIdClient(any(String.class)))
        .thenReturn(java.util.Optional.ofNullable(clientEntity));
    clientEntity.setEmailVerified(true);
    when(clientsRepository.save(any(ClientEntity.class))).thenReturn(clientEntity);
    testedClass.getMessage(new HashMap<>(), eventRequest);
    assertTrue(clientEntity.getEmailVerified());
  }

  @Test()
  public void shouldDONTUPDATEEMAILSinceClientDoesNotExist() throws JsonProcessingException {
    eventEmailVerified.setId(String.valueOf(System.currentTimeMillis()));
    eventEmailVerified.setEventType(EmailVerified.class.getSimpleName());
    eventEmailVerified.setPayload(epayload);
    ObjectMapper objectMapper = new ObjectMapper();
    String eventRequest = objectMapper.writeValueAsString(eventEmailVerified);
    ClientEntity clientNotFound = null;
    when(clientsRepository.findByIdClient(any(String.class)))
        .thenReturn(java.util.Optional.ofNullable(clientNotFound));
    when(clientsRepository.save(any(ClientEntity.class))).thenReturn(clientEntity);
    testedClass.getMessage(new HashMap<>(), eventRequest);
    assertNull(clientEntity.getEmailVerified());
  }


  private void prepareTestToHandleErrorSendingToRiskEngineWithRetry() {
    Map<Integer, Integer> delayOptions = new HashMap<>();
    delayOptions.put(1, 5);
    retriesOption = new RetriesOption(1, delayOptions);
    testedClass = new SqsClientListenerAdapter(clientsRepository, null, null, null,null);
  }
}
