package com.lulobank.clients.services.inboundadapters;

import static com.lulobank.clients.services.Constants.ACESS_TOKEN;
import static com.lulobank.clients.services.Constants.AMOUNT;
import static com.lulobank.clients.services.Constants.INSTALLMENTS;
import static com.lulobank.clients.services.Constants.INTEREST_RATE;
import static com.lulobank.clients.services.Constants.MAX_AMOUNT_INSTALLMENT;
import static com.lulobank.clients.services.Constants.PURPOSE;
import static com.lulobank.clients.services.Sample.clientEntityBuilder;
import static com.lulobank.clients.services.Sample.loanClientRequestedBuilder;
import static com.lulobank.clients.services.Sample.loanRequestedBuilder;
import static com.lulobank.clients.services.Sample.riskScoringResultEventBuilder;
import static com.lulobank.clients.services.utils.ClientHelper.LOANREQUESTED_VERIFICATION;
import static com.lulobank.clients.services.utils.LoanRequestedStatus.FINISHED;
import static com.lulobank.clients.services.utils.LoanRequestedStatus.IN_PROGRESS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.DatabaseReference;
import com.lulobank.clients.services.events.Results;
import com.lulobank.clients.services.events.RiskScoringResult;
import com.lulobank.clients.services.features.riskscoreresponse.RiskScoringResultEventFactory;
import com.lulobank.clients.services.features.riskscoreresponse.RiskScoringResultFromHomeEventHandler;
import com.lulobank.clients.services.features.riskscoreresponse.RiskScoringResultFromOnbordingEventHandler;
import com.lulobank.clients.services.outboundadapters.ClientsOutboundAdapter;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.model.LoanRequested;
import com.lulobank.clients.services.outboundadapters.model.OnBoardingStatus;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.clients.services.utils.ClientErrorResultsEnum;
import com.lulobank.clients.services.utils.ClientHelper;
import com.lulobank.clients.services.utils.ClientVerificationFirebase;
import com.lulobank.clients.services.utils.LoanRequestedStatusFirebase;
import com.lulobank.clients.services.utils.LuloUserTokenGenerator;
import com.lulobank.clients.services.utils.ProductTypeEnum;
import com.lulobank.clients.services.utils.StatusClientVerificationFirebaseEnum;
import com.lulobank.core.events.Event;
import com.lulobank.core.utils.OnboardingStatus;
import com.lulobank.credits.sdk.dto.initialofferv2.GetOfferToClient;
import com.lulobank.credits.sdk.exceptions.InitialOffersException;
import com.lulobank.credits.sdk.operations.InitialOffersOperations;
import com.nimbusds.jose.JOSEException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class SqsClientListenerRiskResponseAdapterTest {

  @Mock private DatabaseReference databaseReference;
  @Mock private InitialOffersOperations initialOffersOperations;
  @Mock private LuloUserTokenGenerator luloUserTokenGenerator;
  @Mock private ClientsRepository clientsRepository;
  @InjectMocks private ClientsOutboundAdapter clientsOutboundAdapter;
  private Event<RiskScoringResult> riskScoringResultEvent;
  private RiskScoringResult riskScoringResult;
  private ClientEntity clientEntity;
  private SqsRiskEngineListenerAdapter testedClass;
  private RiskScoringResultFromHomeEventHandler riskScoringResultFromHomeEventHandler;
  private RiskScoringResultFromOnbordingEventHandler riskScoringResultFromOnbordingEventHandler;
  @Captor protected ArgumentCaptor<ClientEntity> clientEntityCaptor;
  @Captor protected ArgumentCaptor<String> childFirebase;
  @Captor protected ArgumentCaptor<Map<String, Object>> updateChild;
  private ObjectMapper objectMapper;
  @Mock private RiskScoringResultEventFactory riskScoringResultEventFactory;
  @Captor protected ArgumentCaptor<HashMap> hashMapArgumentCaptor;
  @Captor protected ArgumentCaptor<GetOfferToClient> getOfferToClient;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    riskScoringResultFromHomeEventHandler =
        new RiskScoringResultFromHomeEventHandler(clientsOutboundAdapter);
    riskScoringResultFromOnbordingEventHandler =
        new RiskScoringResultFromOnbordingEventHandler(clientsOutboundAdapter);
    riskScoringResultEventFactory =
        new RiskScoringResultEventFactory(
            clientsRepository,
            riskScoringResultFromHomeEventHandler,
            riskScoringResultFromOnbordingEventHandler);
    testedClass = new SqsRiskEngineListenerAdapter(riskScoringResultEventFactory);
    riskScoringResult =
        riskScoringResultEventBuilder(AMOUNT, INSTALLMENTS, MAX_AMOUNT_INSTALLMENT, INTEREST_RATE);
    clientEntity = clientEntityBuilder();
    objectMapper = new ObjectMapper();
    when(luloUserTokenGenerator.getUserToken(anyString())).thenReturn(ACESS_TOKEN);
  }

  @Test
  public void flow_credit_from_home_ok() throws JsonProcessingException {
    setDataToFlowCreditFromHomeOk();
    when(clientsRepository.findByIdClient(any(String.class)))
        .thenReturn(java.util.Optional.ofNullable(clientEntity));
    when(initialOffersOperations.initialOffers(
            any(HashMap.class), any(GetOfferToClient.class), anyString()))
        .thenReturn(true);
    when(clientsRepository.save(any(ClientEntity.class))).thenReturn(clientEntity);
    when(databaseReference.child(anyString())).thenReturn(databaseReference);
    String eventRequest = objectMapper.writeValueAsString(riskScoringResultEvent);
    testedClass.getRiskResponse(new HashMap<>(), eventRequest);

    Mockito.verify(clientsOutboundAdapter.getClientsRepository(), times(1))
        .save(clientEntityCaptor.capture());
    Mockito.verify(databaseReference, times(2)).child(childFirebase.capture());
    Mockito.verify(databaseReference, times(1)).updateChildrenAsync(updateChild.capture());
    Mockito.verify(initialOffersOperations, times(1))
        .initialOffers(hashMapArgumentCaptor.capture(), getOfferToClient.capture(), anyString());
    List<String> firebaseUdpate = childFirebase.getAllValues();
    ClientEntity clientEntitySave = clientEntityCaptor.getValue();
    Map<String, Object> updateChildvalue = updateChild.getValue();
    LoanRequestedStatusFirebase firebaseUpdateValue =
        (LoanRequestedStatusFirebase) updateChildvalue.get("loanVerification");
    GetOfferToClient getOfferToClientValue = getOfferToClient.getValue();
    assertTrue(
        "Risk score was save",
        clientEntity.getCreditRiskAnalysis().getStatus().equals(riskScoringResult.getStatus()));
    assertEquals(
        "Loan Requested Status", FINISHED.name(), clientEntitySave.getLoanRequested().getStatus());
    assertTrue(
        "Firebase child contains",
        firebaseUdpate.contains(ClientHelper.REFERENCE_LOAN_REQUESTED_FIREBASE_CLIENTS));
    assertTrue("Firebase child contains", firebaseUdpate.contains(clientEntity.getIdClient()));
    assertEquals(
        "Firebase update child verification result select",
        "FINISHED",
        firebaseUpdateValue.getStatus());
    Results creditRiskAnalysisResult =
        riskScoringResultEvent.getPayload().getResults().stream().findFirst().get();
    assertEquals(
        "Inital Offers Risk Engine amount right",
        creditRiskAnalysisResult.getAmount(),
        getOfferToClientValue.getRiskEngineAnalysis().getAmount());
    assertEquals(
        "Inital Offers Risk Engine amount installments right",
        creditRiskAnalysisResult.getMaxAmountInstallment(),
        getOfferToClientValue.getRiskEngineAnalysis().getMaxAmountInstallment());
    assertEquals(
        "Inital Offers Risk Engine interest rate right",
        creditRiskAnalysisResult.getInterestRate(),
        getOfferToClientValue.getRiskEngineAnalysis().getInterestRate());
  }

  @Test
  public void economicInformationIsNotPresent() throws JsonProcessingException {
    setDataToFlowCreditFromHomeOk();
    clientEntity.setEconomicInformation(null);
    when(clientsRepository.findByIdClient(any(String.class)))
        .thenReturn(java.util.Optional.ofNullable(clientEntity));
    String eventRequest = objectMapper.writeValueAsString(riskScoringResultEvent);
    testedClass.getRiskResponse(new HashMap<>(), eventRequest);
    Mockito.verify(clientsOutboundAdapter.getClientsRepository(), never())
        .save(any(ClientEntity.class));
    Mockito.verify(initialOffersOperations, never()).initialOffers(anyMap(), any(),any());
  }

  @Test
  public void flow_onboarding_credits() throws JsonProcessingException, JOSEException {
    setDataToFlowObordingCredits();
    when(clientsRepository.findByIdClient(any(String.class)))
        .thenReturn(java.util.Optional.ofNullable(clientEntity));
    when(initialOffersOperations.initialOffers(
            any(HashMap.class), any(GetOfferToClient.class), anyString()))
        .thenReturn(true);
    when(clientsRepository.save(any(ClientEntity.class))).thenReturn(clientEntity);
    when(databaseReference.child(anyString())).thenReturn(databaseReference);
    String eventRequest = objectMapper.writeValueAsString(riskScoringResultEvent);
    testedClass.getRiskResponse(new HashMap<>(), eventRequest);
    Mockito.verify(clientsOutboundAdapter.getClientsRepository(), times(1))
        .save(clientEntityCaptor.capture());
    Mockito.verify(databaseReference, times(2)).child(childFirebase.capture());
    Mockito.verify(databaseReference, times(1)).updateChildrenAsync(updateChild.capture());
    List<String> firebaseUdpate = childFirebase.getAllValues();
    ClientEntity clientEntitySave = clientEntityCaptor.getValue();
    Map<String, Object> updateChildvalue = updateChild.getValue();

    assertTrue(
        "Risk score was save",
        clientEntity.getCreditRiskAnalysis().getStatus().equals(riskScoringResult.getStatus()));
    assertTrue("Firebase child contains", firebaseUdpate.contains("on_boarding/clients"));
    assertTrue("Firebase child contains", firebaseUdpate.contains(clientEntity.getIdClient()));
    ClientVerificationFirebase clientVerificationFirebase =
        (ClientVerificationFirebase) updateChildvalue.get("clientVerification");
    assertEquals(
        "Firebase update child product select",
        "CREDIT_ACCOUNT",
        clientVerificationFirebase.getProductSelected());
    assertEquals(
        "Firebase update child verification result select",
        StatusClientVerificationFirebaseEnum.OK.name(),
        clientVerificationFirebase.getVerificationResult());
  }

  @Test
  public void shouldThrowServicesException_since_onbording_is_finished()
      throws JsonProcessingException {
    riskScoringResultEvent = riskScoringResultEventBuilder(riskScoringResult);
    LoanRequested loanRequested = loanRequestedBuilder(FINISHED);
    clientEntity.setLoanRequested(loanRequested);
    when(clientsRepository.findByIdClient(any(String.class)))
        .thenReturn(java.util.Optional.ofNullable(clientEntity));
    String eventRequest = objectMapper.writeValueAsString(riskScoringResultEvent);
    testedClass.getRiskResponse(new HashMap<>(), eventRequest);
    assertEquals("", FINISHED.name(), clientEntity.getLoanRequested().getStatus());
  }

  @Test
  public void shouldThrowServicesException_since_loanrequested_is_finished()
      throws JsonProcessingException {
    riskScoringResultEvent = riskScoringResultEventBuilder(riskScoringResult);
    clientEntity.setOnBoardingStatus(
        new OnBoardingStatus(
            OnboardingStatus.FINISH_ON_BOARDING.name(), ProductTypeEnum.CREDIT_ACCOUNT.name()));
    when(clientsRepository.findByIdClient(any(String.class)))
        .thenReturn(java.util.Optional.ofNullable(clientEntity));
    String eventRequest = objectMapper.writeValueAsString(riskScoringResultEvent);
    testedClass.getRiskResponse(new HashMap<>(), eventRequest);
    assertEquals(
        "Onbording is finished",
        OnboardingStatus.FINISH_ON_BOARDING.name(),
        clientEntity.getOnBoardingStatus().getCheckpoint());
  }

  @Test
  public void shouldThrowClientNotFound_in_RiskEvents() throws JsonProcessingException {
    riskScoringResultEvent = riskScoringResultEventBuilder(riskScoringResult);
    String eventRequest = objectMapper.writeValueAsString(riskScoringResultEvent);
    testedClass.getRiskResponse(new HashMap<>(), eventRequest);
    verify(clientsRepository, never()).save(any(ClientEntity.class));
  }

  @Test
  public void flowOnBoardingCreditsFailsCallingInitialOffer() throws JsonProcessingException {
    setDataToFlowObordingCredits();
    when(clientsRepository.findByIdClient(any(String.class)))
        .thenReturn(java.util.Optional.ofNullable(clientEntity));
    when(initialOffersOperations.initialOffers(
            any(HashMap.class), any(GetOfferToClient.class), anyString()))
        .thenThrow(new InitialOffersException(500, "InitialOffer failed!"));
    when(databaseReference.child(anyString())).thenReturn(databaseReference);

    String eventRequest = objectMapper.writeValueAsString(riskScoringResultEvent);
    testedClass.getRiskResponse(new HashMap<>(), eventRequest);
    Mockito.verify(databaseReference, times(2)).child(childFirebase.capture());
    Mockito.verify(databaseReference, times(1)).updateChildrenAsync(updateChild.capture());
    Map<String, Object> updateChildvalue = updateChild.getValue();
    ClientVerificationFirebase clientVerificationFirebase =
        (ClientVerificationFirebase) updateChildvalue.get("clientVerification");
    assertEquals(
        StatusClientVerificationFirebaseEnum.FAILED.name(),
        clientVerificationFirebase.getVerificationResult());
    assertEquals(
        ClientErrorResultsEnum.ERROR_LOAN_IN_OFFER_GENERATION.name(),
        clientVerificationFirebase.getDetail());
  }

  @Test
  public void flowFromHomeCreditsFailsCallingInitialOffer() throws JsonProcessingException {
    setDataToFlowCreditFromHomeOk();
    when(clientsRepository.findByIdClient(any(String.class)))
        .thenReturn(java.util.Optional.ofNullable(clientEntity));
    when(initialOffersOperations.initialOffers(
            any(HashMap.class), any(GetOfferToClient.class), anyString()))
        .thenThrow(new InitialOffersException(500, "InitialOffer failed!"));
    when(databaseReference.child(anyString())).thenReturn(databaseReference);

    String eventRequest = objectMapper.writeValueAsString(riskScoringResultEvent);
    testedClass.getRiskResponse(new HashMap<>(), eventRequest);

    Mockito.verify(databaseReference, times(2)).child(childFirebase.capture());
    Mockito.verify(databaseReference, times(1)).updateChildrenAsync(updateChild.capture());
    Map<String, Object> updateChildvalue = updateChild.getValue();
    LoanRequestedStatusFirebase loanRequestedStatusFirebase =
        (LoanRequestedStatusFirebase) updateChildvalue.get(LOANREQUESTED_VERIFICATION);
    assertEquals(
        StatusClientVerificationFirebaseEnum.FAILED.name(),
        loanRequestedStatusFirebase.getStatus());
    assertEquals(
        ClientErrorResultsEnum.ERROR_LOAN_IN_OFFER_GENERATION.name(),
        loanRequestedStatusFirebase.getDetail());
  }

  private void setDataToFlowCreditFromHomeOk() {
    riskScoringResultEvent = riskScoringResultEventBuilder(riskScoringResult);
    LoanRequested loanRequested =
        loanRequestedBuilder(loanClientRequestedBuilder(AMOUNT, PURPOSE), IN_PROGRESS);
    clientEntity.setLoanRequested(loanRequested);
    clientEntity.setOnBoardingStatus(
        new OnBoardingStatus(
            OnboardingStatus.FINISH_ON_BOARDING.name(), ProductTypeEnum.SAVING_ACCOUNT.name()));
  }

  private void setDataToFlowObordingCredits() {
    clientEntity.setOnBoardingStatus(
        new OnBoardingStatus(
            OnboardingStatus.ON_BOARDING.name(), ProductTypeEnum.CREDIT_ACCOUNT.name()));
    riskScoringResultEvent = riskScoringResultEventBuilder(riskScoringResult);
    clientEntity
        .getOnBoardingStatus()
        .setLoanClientRequested(loanClientRequestedBuilder(AMOUNT, PURPOSE));
  }
}
