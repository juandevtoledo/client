package com.lulobank.clients.starter;

import com.amazonaws.SdkClientException;
import com.lulobank.clients.sdk.operations.dto.economicinformation.CheckingAccount;
import com.lulobank.clients.sdk.operations.dto.economicinformation.ClientEconomicInformation;
import com.lulobank.clients.sdk.operations.dto.economicinformation.EmployeeCompany;
import com.lulobank.clients.sdk.operations.dto.economicinformation.ForeignCurrencyTransaction;
import com.lulobank.clients.sdk.operations.dto.economicinformation.OccupationType;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.model.OnBoardingStatus;
import com.lulobank.core.events.Event;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.lulobank.clients.sdk.operations.util.CheckPoints.FINISH_ON_BOARDING;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 *  @deprecated as security issues were identified with the related endpoint
 *  Please remove once the  <tt>/clients/onboarding/economicinformation</tt> endpoint has been completely unused
 */
public class OnboardingEconomicInformationTest extends AbstractBaseIntegrationTest {
  private static final String TESTED_URL = "/onboarding/economicinformation";
  private ClientEconomicInformation clientEconomicInformation;
  private ClientEntity clientEntityFound;

  @Value("classpath:mocks/onboardingclients/ClientOnboardingClientNotFoundResponse.json")
  private Resource responseClientOnboardingNotFound;

  @Value("classpath:mocks/onboardingclients/EconomicRequestRetiredType.json")
  private Resource economicRequestRetiredType;

  @Value("classpath:mocks/onboardingclients/EconomicRequestSelfEmployeeType.json")
  private Resource economicRequestSelfEmployeeType;

  @Override
  protected void init() {
    clientEntityFound = new ClientEntity();
    clientEntityFound.setIdClient("1234-5432");
    clientEntityFound.setDateOfIssue(LocalDate.now());
    clientEntityFound.setIdCard("12345678");
    clientEntityFound.setIdentityProcessed(true);
    OnBoardingStatus onBoardingStatus = new OnBoardingStatus();
    onBoardingStatus.setCheckpoint(FINISH_ON_BOARDING.name());
    clientEntityFound.setOnBoardingStatus(onBoardingStatus);
    clientEconomicInformation = new ClientEconomicInformation();
    clientEconomicInformation.setIdClient(ID_CLIENT);
    clientEconomicInformation.setAssets(new BigDecimal(100000000));
    clientEconomicInformation.setLiabilities(new BigDecimal(1000000));
    clientEconomicInformation.setMonthlyOutcome(new BigDecimal(5000000));
    clientEconomicInformation.setMonthlyOutcome(new BigDecimal(1000000));
    clientEconomicInformation.setOccupationType(OccupationType.EMPLOYEE);
    clientEconomicInformation.setAdditionalIncome(BigDecimal.ZERO);
    clientEconomicInformation.setMonthlyIncome(new BigDecimal(15000000));
    clientEconomicInformation.setTypeSaving("typeSaving");
    clientEconomicInformation.setSavingPurpose("savingPurpose");
    clientEconomicInformation.setEconomicActivity("1105");

    EmployeeCompany employeeCompany = new EmployeeCompany();
    employeeCompany.setCity("Bogota");
    employeeCompany.setState("Bogota");
    employeeCompany.setName("Globant");
    clientEconomicInformation.setEmployeeCompany(employeeCompany);
  }

  @Test
  public void shouldReturnAcceptedOnEconomicInfoForeignTransacNull() throws Exception {
    when(clientsRepository.findByIdClient(any(String.class)))
        .thenReturn(Optional.of(clientEntityFound));
    when(clientsRepository.save(any(ClientEntity.class))).thenReturn(clientEntityFound);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(TESTED_URL)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(clientEconomicInformation)))
        .andExpect(status().isCreated());
    Mockito.verify(clientsRepository, times(1)).save(clientEntityCaptor.capture());
    Mockito.verify(queueMessagingTemplate, times(2))
        .convertAndSend(any(String.class), any(Event.class), anyMap());
  }

  @Test
  public void shouldReturnAcceptedOnEconomicInfo() throws Exception {
    clientEconomicInformation.setForeignCurrencyTransactions(getForeignCurrencyTransactionsMock());
    when(clientsRepository.findByIdClient(any(String.class)))
        .thenReturn(Optional.of(clientEntityFound));
    when(clientsRepository.save(any(ClientEntity.class))).thenReturn(clientEntityFound);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(TESTED_URL)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(clientEconomicInformation)))
        .andExpect(status().isCreated());
    Mockito.verify(clientsRepository, times(1)).save(clientEntityCaptor.capture());
    Mockito.verify(queueMessagingTemplate, times(2))
        .convertAndSend(any(String.class), any(Event.class), anyMap());
  }

  @Test
  public void shouldReturnNotFoundErrorSavingInDB() throws Exception {
    when(clientsRepository.findByIdClient(any(String.class)))
        .thenReturn(Optional.of(clientEntityFound));
    when(clientsRepository.save(any(ClientEntity.class)))
        .thenThrow(new SdkClientException("ERROR SAVING"));
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(TESTED_URL)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(clientEconomicInformation)))
        .andExpect(status().isInternalServerError());
  }

  @Test
  public void shouldReturnNotFoundClientNotExistInDBOnEconomicInfo() throws Exception {
    when(clientsRepository.findByIdClient(any(String.class))).thenReturn(Optional.empty());
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(TESTED_URL)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(clientEconomicInformation)))
        .andExpect(status().isNotFound())
        .andExpect(
            content()
                .json(
                    FileUtils.readFileToString(
                        responseClientOnboardingNotFound.getFile(), StandardCharsets.UTF_8)));
  }

  private List<ForeignCurrencyTransaction> getForeignCurrencyTransactionsMock() {
    ForeignCurrencyTransaction foreignCurrencyTransaction = new ForeignCurrencyTransaction();
    foreignCurrencyTransaction.setName("Name");
    foreignCurrencyTransaction.setCheckingAccount(new CheckingAccount());
    foreignCurrencyTransaction.getCheckingAccount().setAmount(new BigDecimal(1000));
    foreignCurrencyTransaction.getCheckingAccount().setBank("Sudameris");
    foreignCurrencyTransaction.getCheckingAccount().setCity("New York");
    foreignCurrencyTransaction.getCheckingAccount().setCountry("EEUU");
    foreignCurrencyTransaction.getCheckingAccount().setCurrency("USD");
    foreignCurrencyTransaction.getCheckingAccount().setNumber("1234567890");
    return new ArrayList<ForeignCurrencyTransaction>(Arrays.asList(foreignCurrencyTransaction));
  }

  @Test
  public void shouldReturnAcceptedOnEconomicInfoRetiredType() throws Exception {
    when(clientsRepository.findByIdClient(any(String.class)))
        .thenReturn(Optional.of(clientEntityFound));
    when(clientsRepository.save(any(ClientEntity.class))).thenReturn(clientEntityFound);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(TESTED_URL)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(
                    FileUtils.readFileToString(
                        economicRequestRetiredType.getFile(), StandardCharsets.UTF_8)))
        .andExpect(status().isCreated());
    Mockito.verify(clientsRepository, times(1)).save(clientEntityCaptor.capture());
    Mockito.verify(queueMessagingTemplate, times(2))
        .convertAndSend(any(String.class), any(Event.class), anyMap());
  }

  @Test
  public void shouldReturnAcceptedOnEconomicInfoSelfEmployeeType() throws Exception {
    when(clientsRepository.findByIdClient(any(String.class)))
        .thenReturn(Optional.of(clientEntityFound));
    when(clientsRepository.save(any(ClientEntity.class))).thenReturn(clientEntityFound);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(TESTED_URL)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(
                    FileUtils.readFileToString(
                        economicRequestSelfEmployeeType.getFile(), StandardCharsets.UTF_8)))
        .andExpect(status().isCreated());
    Mockito.verify(clientsRepository, times(1)).save(clientEntityCaptor.capture());
    Mockito.verify(queueMessagingTemplate, times(2))
        .convertAndSend(any(String.class), any(Event.class), anyMap());
  }
}
