package com.lulobank.clients.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lulobank.clients.sdk.operations.dto.ClientLoanRequested;
import com.lulobank.clients.sdk.operations.dto.economicinformation.ClientEconomicInformation;
import com.lulobank.clients.sdk.operations.dto.economicinformation.EmployeeCompany;
import com.lulobank.clients.sdk.operations.dto.economicinformation.OccupationType;
import com.lulobank.clients.services.domain.RiskLevelBlackList;
import com.lulobank.clients.services.domain.StateBlackList;
import com.lulobank.clients.services.events.AdditionalPersonalInformation;
import com.lulobank.clients.services.events.BlacklistResult;
import com.lulobank.clients.services.events.ClientPersonalInformationResult;
import com.lulobank.clients.services.events.ClientVerificationResult;
import com.lulobank.clients.services.events.IdDocument;
import com.lulobank.clients.services.events.Results;
import com.lulobank.clients.services.events.RiskScoringResult;
import com.lulobank.clients.services.events.UpdateClientAddressEvent;
import com.lulobank.clients.services.features.identitybiometric.model.UpdateIdTransactionBiometric;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.model.CreditRiskAnalysis;
import com.lulobank.clients.services.outboundadapters.model.EconomicInformation;
import com.lulobank.clients.services.outboundadapters.model.IdentityBiometric;
import com.lulobank.clients.services.outboundadapters.model.LoanClientRequested;
import com.lulobank.clients.services.outboundadapters.model.LoanRequested;
import com.lulobank.clients.services.outboundadapters.model.OnBoardingStatus;
import com.lulobank.clients.services.outboundadapters.model.Result;
import com.lulobank.clients.services.outboundadapters.model.TransactionState;
import com.lulobank.clients.services.utils.BiometricResultCodes;
import com.lulobank.clients.services.utils.LoanRequestedStatus;
import com.lulobank.clients.services.utils.ProductTypeEnum;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.LoanRequestedV3;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.OnBoardingStatusV3;
import com.lulobank.clients.v3.adapters.port.out.savingsaccount.dto.SavingsAccountResponse;
import com.lulobank.core.events.Event;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.lulobank.clients.sdk.operations.dto.economicinformation.OccupationType.RETIRED;
import static com.lulobank.clients.sdk.operations.util.CheckPoints.FINISH_ON_BOARDING;
import static com.lulobank.clients.sdk.operations.util.CheckPoints.ON_BOARDING;
import static com.lulobank.clients.services.Constants.ACCOUNT_ID;
import static com.lulobank.clients.services.Constants.BIRTH_DATE_ENTITY;
import static com.lulobank.clients.services.Constants.DATE_ISSUE;
import static com.lulobank.clients.services.Constants.DATE_ISSUE_ENTITY;
import static com.lulobank.clients.services.Constants.EMAIL;
import static com.lulobank.clients.services.Constants.GENDER;
import static com.lulobank.clients.services.Constants.ID_CARD;
import static com.lulobank.clients.services.Constants.ID_CBS;
import static com.lulobank.clients.services.Constants.ID_CLIENT;
import static com.lulobank.clients.services.Constants.ID_TRANSACTION;
import static com.lulobank.clients.services.Constants.LAST_NAME;
import static com.lulobank.clients.services.Constants.NAME;
import static com.lulobank.clients.services.Constants.PHONE;
import static com.lulobank.clients.services.Constants.PREFIX;
import static com.lulobank.clients.services.domain.DocumentType.CC;
import static com.lulobank.clients.services.utils.LoanRequestedStatus.IN_PROGRESS;
import static org.springframework.util.ResourceUtils.getFile;

public class Sample {


  private Sample() {}

  private static ObjectMapper objectMapper = new ObjectMapper();

  static {
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  public static final EconomicInformation economicInformationBuilder(
      OccupationType occupation) {
    EconomicInformation economicInformation = new EconomicInformation();
    economicInformation.setOccupationType(occupation.name());
    return economicInformation;
  }

  public static final RiskScoringResult riskScoringResultEventBuilder(
      Double amount, Integer installments, Double maxAmountInstallment, Float interestRate) {
    RiskScoringResult riskScoringResult = new RiskScoringResult();
    riskScoringResult.setStatus("COMPLETED");
    List<Results> resultList = new ArrayList<>();
    Results result = new Results();
    result.setAmount(amount);
    result.setInstallments(installments);
    result.setMaxAmountInstallment(maxAmountInstallment);
    result.setInterestRate(interestRate);
    result.setType("dummy");
    resultList.add(result);
    riskScoringResult.setResults(resultList);
    return riskScoringResult;
  }

  public static final LoanClientRequested loanClientRequestedBuilder(
      Double amount, String purpose) {
    LoanClientRequested loanClientRequested = new LoanClientRequested();
    loanClientRequested.setAmount(amount);
    loanClientRequested.setLoanPurpose(purpose);
    LoanRequested loanRequested = new LoanRequested();
    loanRequested.setStatus(IN_PROGRESS.name());
    loanRequested.setLoanClientRequested(loanClientRequested);
    return loanClientRequested;
  }

  public static final Event<RiskScoringResult> riskScoringResultEventBuilder(
      RiskScoringResult riskScoringResult) {
    Event<RiskScoringResult> riskScoringResultEvent = new Event<>();
    riskScoringResultEvent.setId(UUID.randomUUID().toString());
    riskScoringResultEvent.setEventType(RiskScoringResult.class.getSimpleName());
    riskScoringResultEvent.setPayload(riskScoringResult);
    return riskScoringResultEvent;
  };

  public static final LoanRequested loanRequestedBuilder(LoanRequestedStatus status) {
    return loanRequestedBuilder(null, status);
  }

  public static final LoanRequested loanRequestedBuilder(
      LoanClientRequested loanClientRequested, LoanRequestedStatus status) {
    LoanRequested loanRequested = new LoanRequested();
    loanRequested.setStatus(status.name());
    loanRequested.setLoanClientRequested(loanClientRequested);
    return loanRequested;
  }

  public static final ClientEntity clientEntityBuilder() {
    ClientEntity clientEntity = new ClientEntity();
    clientEntity.setIdClient(ID_CLIENT);
    clientEntity.setIdCard(ID_CARD);
    clientEntity.setEconomicInformation(economicInformationBuilder(RETIRED));
    clientEntity.setOnBoardingStatus(onBoardingStatusBuilder());
    clientEntity.setName(NAME);
    clientEntity.setLastName(LAST_NAME);
    clientEntity.setPhoneNumber(PHONE);
    clientEntity.setPhonePrefix(PREFIX);
    clientEntity.setDateOfIssue(LocalDate.parse(DATE_ISSUE_ENTITY));
    clientEntity.setBirthDate(LocalDate.parse(BIRTH_DATE_ENTITY));
    clientEntity.setGender(GENDER);
    clientEntity.setTypeDocument(CC.name());
    clientEntity.setEmailAddress(EMAIL);
    return clientEntity;
  }
  
  public static final OnBoardingStatus onBoardingStatusBuilder() {
	  OnBoardingStatus boardingStatus = new OnBoardingStatus();
	  boardingStatus.setCheckpoint(FINISH_ON_BOARDING.name());
	  return boardingStatus;
  }

  public static final ClientEntity clientEntityBuilder(OnBoardingStatus onBoardingStatus, CreditRiskAnalysis riskEngineAnalysis) {
    ClientEntity clientEntity = new ClientEntity();
    clientEntity.setIdClient(ID_CLIENT);
    clientEntity.setIdCard(ID_CARD);
    clientEntity.setEconomicInformation(economicInformationBuilder(RETIRED));
    clientEntity.setOnBoardingStatus(onBoardingStatus);
    clientEntity.setCreditRiskAnalysis(riskEngineAnalysis);
    return clientEntity;
  }

  public static final ClientEntity clientEntityBuilder(LoanRequested loanRequested, CreditRiskAnalysis riskEngineAnalysis) {
    ClientEntity clientEntity = new ClientEntity();
    clientEntity.setIdClient(ID_CLIENT);
    clientEntity.setIdCard(ID_CARD);
    clientEntity.setEconomicInformation(economicInformationBuilder(RETIRED));
    clientEntity.setLoanRequested(loanRequested);
    clientEntity.setCreditRiskAnalysis(riskEngineAnalysis);
    return clientEntity;
  }

  public static final ClientEntity clientEntityBuilder(OnBoardingStatus onBoardingStatus,IdentityBiometric identityBiometric) {
    ClientEntity clientEntity = new ClientEntity();
    clientEntity.setIdClient(ID_CLIENT);
    clientEntity.setIdCard(ID_CARD);
    clientEntity.setEconomicInformation(economicInformationBuilder(RETIRED));
    clientEntity.setOnBoardingStatus(onBoardingStatus);
    clientEntity.setIdentityBiometric(identityBiometric);
    clientEntity.setPhoneNumber(Constants.PHONE);
    clientEntity.setPhonePrefix(Constants.PREFIX);
    return clientEntity;
  }
  public static final OnBoardingStatus onBoardingStatusBuilder(ProductTypeEnum type,LoanClientRequested loanClientRequested,String checkPoint){
    OnBoardingStatus onBoardingStatus=new OnBoardingStatus();
    onBoardingStatus.setProductSelected(type.name());
    onBoardingStatus.setLoanClientRequested(loanClientRequested);
    onBoardingStatus.setCheckpoint(checkPoint);
    return onBoardingStatus;
  }

  public static final OnBoardingStatus onBoardingStatusBuilder(ProductTypeEnum type){
    OnBoardingStatus onBoardingStatus=new OnBoardingStatus();
    onBoardingStatus.setCheckpoint(ON_BOARDING.name());
    onBoardingStatus.setProductSelected(type.name());
    return onBoardingStatus;
  }
  public static final LoanClientRequested loanClientRequestedBuilder(String purpose,Double amount){
    LoanClientRequested loanClientRequested = new LoanClientRequested();
    loanClientRequested.setLoanPurpose(purpose);
    loanClientRequested.setAmount(amount);
    return loanClientRequested;
  }

  public static final CreditRiskAnalysis creditRiskAnalysisBuilder(Double amount,Double maxAmountInstallment,Float interestRate){
    CreditRiskAnalysis creditRiskAnalysis=new CreditRiskAnalysis();
    creditRiskAnalysis.setStatus("OK");
    Result result = new Result();
    result.setAmount(amount);
    result.setMaxAmountInstallment(maxAmountInstallment);
    result.setInterestRate(interestRate);
    result.setType("dummy");
    List<Result> results=new ArrayList<>();
    results.add(result);
    creditRiskAnalysis.setResults(results);
    return creditRiskAnalysis;
  }

  public static final ClientLoanRequested clientLoanRequestedBuilder(String idClient,Double amount,String loanPurpose) {
    ClientLoanRequested clientLoanRequested=new ClientLoanRequested();
    clientLoanRequested.setIdClient(idClient);
    clientLoanRequested.setAmount(amount);
    clientLoanRequested.setLoanPurpose(loanPurpose);
    return clientLoanRequested;
  }

  public static final ClientPersonalInformationResult clientPersonalInformationResultBuilder(String name, String lastName, String gender, IdDocument idDocument){
    ClientPersonalInformationResult clientPersonalInformationResult=new ClientPersonalInformationResult();
    clientPersonalInformationResult.setName(name);
    clientPersonalInformationResult.setLastName(lastName);
    clientPersonalInformationResult.setGender(gender);
    clientPersonalInformationResult.setIdDocument(idDocument);
    clientPersonalInformationResult.setBirthDate(Constants.BIRTH_DATE);
    clientPersonalInformationResult.setAdditionalPersonalInformation(additionalPersonalInformation(name,lastName));
    return clientPersonalInformationResult;
  }

  public static final AdditionalPersonalInformation additionalPersonalInformation(String name, String lastName){
    AdditionalPersonalInformation additionalPersonalInformation = new AdditionalPersonalInformation();
    additionalPersonalInformation.setFirstName(name);
    additionalPersonalInformation.setFirstSurname(lastName);
    additionalPersonalInformation.setSecondName(" ");
    return additionalPersonalInformation;
  }

  public static final IdentityBiometric identityBiometricBuilder(String idTransaction,String status){
    IdentityBiometric identityBiometric=new IdentityBiometric();
    identityBiometric.setIdTransaction(idTransaction);
    identityBiometric.setStatus(status);
    return identityBiometric;
  }

  public static ClientVerificationResult clientVerificationResultBuilder(String idTransactionBiometric, ClientPersonalInformationResult clientPersonalInformation, BlacklistResult blackListResult){
    ClientVerificationResult clientVerificationResult = new ClientVerificationResult();
    clientVerificationResult.setIdTransactionBiometric(idTransactionBiometric);
    clientVerificationResult.setStatus("OK");
    clientVerificationResult.setClientPersonalInformation(clientPersonalInformation);
    clientVerificationResult.setTransactionState(transactionStateEventBuilder(BiometricResultCodes.SUCCESSFUL));
    clientVerificationResult.setBlacklist(blackListResult);
    AdditionalPersonalInformation additionalPersonalInformation=new AdditionalPersonalInformation();
    additionalPersonalInformation.setFirstName(NAME);
    clientPersonalInformation.setAdditionalPersonalInformation(additionalPersonalInformation);
    return clientVerificationResult;
  }

  public static com.lulobank.clients.services.events.TransactionState transactionStateEventBuilder(BiometricResultCodes code){
    com.lulobank.clients.services.events.TransactionState transactionState=new com.lulobank.clients.services.events.TransactionState();
    transactionState.setStateName(code.name());
    transactionState.setId(code.getCode());
    return transactionState;
  }

  public static TransactionState transactionStateBuilder(BiometricResultCodes code){
    TransactionState transactionState=new TransactionState();
    transactionState.setStateName(code.name());
    transactionState.setId(code.getCode());
    return transactionState;
  }

  public static BlacklistResult blacklistResultBuilder(StateBlackList status){
    BlacklistResult blacklistResult=new BlacklistResult();
    blacklistResult.setStatus(status.name());
    blacklistResult.setResultRiskLevel(RiskLevelBlackList.NO_RISK.getLevel());
    return blacklistResult;
  }

  public static IdDocument idDocumentBuilder(String idCard,String dateIssue){
    IdDocument idDocument=new IdDocument();
    idDocument.setIdCard(idCard);
    idDocument.setDocumentType(CC.name());
    idDocument.setExpeditionDate(dateIssue);
    return idDocument;
  }

  public static SavingsAccountResponse createSavingsResponseBuilder() {
    SavingsAccountResponse createSavingsAccountResponse = new SavingsAccountResponse();
    createSavingsAccountResponse.setIdCbs(ID_CBS);
    createSavingsAccountResponse.setAccountId(ACCOUNT_ID);
    return createSavingsAccountResponse;
  }

  public static UpdateIdTransactionBiometric updateIdTransactionBuilder() {
    UpdateIdTransactionBiometric updateIdTransactionBiometric=new UpdateIdTransactionBiometric();
    updateIdTransactionBiometric.setIdClient(Constants.ID_CLIENT);
    updateIdTransactionBiometric.setIdTransactionBiometric(ID_TRANSACTION);
    return updateIdTransactionBiometric;
  }

  public static ClientsV3Entity buildClientsV3Entity(OnBoardingStatusV3 onBoardingStatusV3, LoanRequestedV3 loanRequestedV3) {
    ClientsV3Entity entity = new ClientsV3Entity();
    entity.setOnBoardingStatus(onBoardingStatusV3);
    entity.setLoanRequested(loanRequestedV3);
    return entity;
  }

  public static ClientEconomicInformation buildClientEconomicInformation () {
    ClientEconomicInformation clientEconomicInformation = new ClientEconomicInformation();
    clientEconomicInformation.setAssets(new BigDecimal(100000000));
    clientEconomicInformation.setLiabilities(new BigDecimal(1000000));
    clientEconomicInformation.setMonthlyOutcome(new BigDecimal(5000000));
    clientEconomicInformation.setOccupationType(OccupationType.EMPLOYEE);
    clientEconomicInformation.setAdditionalIncome(BigDecimal.ZERO);
    clientEconomicInformation.setMonthlyIncome(new BigDecimal(15000000));
    clientEconomicInformation.setIdClient(ID_CLIENT);
    clientEconomicInformation.setEconomicActivity("2029");
    clientEconomicInformation.setSavingPurpose("Purpose Test");
    clientEconomicInformation.setTypeSaving("Type Test");
    EmployeeCompany employeeCompany = new EmployeeCompany();
    employeeCompany.setCity("Bogota");
    employeeCompany.setState("Bogota");
    employeeCompany.setName("LuloBank");
    clientEconomicInformation.setEmployeeCompany(employeeCompany);
    return clientEconomicInformation;
  }

  public static UpdateClientAddressEvent updateClientAddressEventBuilder() throws IOException {
    return objectMapper.readValue(getFile("classpath:updateClientAddressEvent.json"), UpdateClientAddressEvent.class);
  }

  public static Event<ClientVerificationResult> getEvent() {
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
