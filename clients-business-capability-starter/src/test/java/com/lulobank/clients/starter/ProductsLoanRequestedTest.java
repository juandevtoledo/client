package com.lulobank.clients.starter;

import static com.lulobank.clients.services.utils.ClientHelper.LOANREQUESTED_VERIFICATION;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.model.IdentityBiometric;
import com.lulobank.clients.services.outboundadapters.model.OnBoardingStatus;
import com.lulobank.clients.services.utils.ClientErrorResultsEnum;
import com.lulobank.clients.services.utils.IdentityBiometricStatus;
import com.lulobank.clients.services.utils.LoanRequestedStatusFirebase;
import com.lulobank.clients.services.utils.ProductTypeEnum;
import com.lulobank.clients.services.utils.StatusClientVerificationFirebaseEnum;

import io.vavr.control.Either;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.messaging.MessagingException;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class ProductsLoanRequestedTest extends AbstractBaseIntegrationTest {

  private static final String TESTED_URL = "/1106bc49-4a0f-4f52-86ca-1994bb3c26d9/products/request/loan";
  private ClientEntity clientEntityFound;
  private String ID_CLIENT = "1106bc49-4a0f-4f52-86ca-1994bb3c26d9";

  @Value("classpath:mocks/productsloanrequested/ProductsLoanRequested-ClientNotFound-Response.json")
  private Resource responseClientNotFound;

  @Value(
      "classpath:mocks/productsloanrequested/ProductsLoanRequested-OnfidoNotFinished-Response.json")
  private Resource responseOnfidoNotFinished;

  @Value(
      "classpath:mocks/productsloanrequested/ProductsLoanRequested-RiskEngineSQSFails-Response.json")
  private Resource responseRiskEngineSQSFails;

  @Override
  protected void init() {
    OnBoardingStatus onBoardingStatus = new OnBoardingStatus();
    onBoardingStatus.setProductSelected(ProductTypeEnum.CREDIT_ACCOUNT.name());
    clientEntityFound = new ClientEntity();
    clientEntityFound.setIdClient(ID_CLIENT);
    clientEntityFound.setDateOfIssue(LocalDate.now());
    clientEntityFound.setIdCard("12345678");
    clientEntityFound.setOnBoardingStatus(onBoardingStatus);
    IdentityBiometric identityBiometric = new IdentityBiometric();
    identityBiometric.setStatus(IdentityBiometricStatus.FINISHED.name());
    clientEntityFound.setIdentityBiometric(identityBiometric);
  }

  @Test
  public void productsRequestLoanOK() throws Exception {
    when(clientsRepository.findByIdClient(any(String.class)))
        .thenReturn(Optional.of(clientEntityFound));
    when(databaseReference.child(any(String.class))).thenReturn(databaseReference);
    when(creditsService.getActiveLoan(any(String.class), any())).thenReturn(Either.left(null));
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(TESTED_URL, ID_CLIENT)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken()))
        .andExpect(status().isAccepted());
    Mockito.verify(clientsRepository, times(1)).save(clientEntityCaptor.capture());
    ClientEntity clientEntityResult = clientEntityCaptor.getValue();
  }

  @Test
  public void productsRequestLoanFailedWhenClientNotFound() throws Exception {
    Optional<ClientEntity> clientEmpty = Optional.empty();
    when(clientsRepository.findByIdClient(any(String.class))).thenReturn(clientEmpty);
    when(databaseReference.child(any(String.class))).thenReturn(databaseReference);
    when(creditsService.getActiveLoan(any(String.class), any())).thenReturn(Either.left(null));
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(TESTED_URL, ID_CLIENT)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken()))
        .andExpect(status().isNotFound())
        .andExpect(
            content()
                .json(
                    FileUtils.readFileToString(
                        responseClientNotFound.getFile(), StandardCharsets.UTF_8)));
  }

  @Test
  public void BiometricHasNotBeenFinished() throws Exception {
    clientEntityFound.getIdentityBiometric().setStatus(IdentityBiometricStatus.IN_PROGRESS.name());
    when(clientsRepository.findByIdClient(any(String.class)))
        .thenReturn(Optional.of(clientEntityFound));
    when(databaseReference.child(any(String.class))).thenReturn(databaseReference);
    when(creditsService.getActiveLoan(any(String.class), any())).thenReturn(Either.left(null));
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(TESTED_URL, ID_CLIENT)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken()))
        .andExpect(status().isBadRequest())
        .andExpect(
            content()
                .json(
                    FileUtils.readFileToString(
                        responseOnfidoNotFinished.getFile(), StandardCharsets.UTF_8)));
  }

  @Test
  public void identityBiometricStatusIsNull() throws Exception {
    clientEntityFound.setIdentityBiometric(null);
    when(clientsRepository.findByIdClient(any(String.class)))
        .thenReturn(Optional.of(clientEntityFound));
    when(databaseReference.child(any(String.class))).thenReturn(databaseReference);
    when(creditsService.getActiveLoan(any(String.class), any())).thenReturn(Either.left(null));
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(TESTED_URL, ID_CLIENT)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken()))
        .andExpect(status().isBadRequest())
        .andExpect(
            content()
                .json(
                    FileUtils.readFileToString(
                        responseOnfidoNotFinished.getFile(), StandardCharsets.UTF_8)));
  }

  @Test
  public void productsRequestLoanFailsWhenSendingToRiskEngineSQS() throws Exception {
    when(clientsRepository.findByIdClient(any(String.class)))
        .thenReturn(Optional.of(clientEntityFound));
    doThrow(new MessagingException("Failed sending to RiskEngine SQS"))
        .when(messageToNotifySQSRiskEngine)
        .run(any(), any());
    when(databaseReference.child(any(String.class))).thenReturn(databaseReference);
    when(creditsService.getActiveLoan(any(String.class), any())).thenReturn(Either.left(null));
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(TESTED_URL, ID_CLIENT)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken()))
        .andExpect(status().isBadGateway())
        .andExpect(
            content()
                .json(
                    FileUtils.readFileToString(
                        responseRiskEngineSQSFails.getFile(), StandardCharsets.UTF_8)));
    verify(databaseReference, times(1)).updateChildrenAsync(firebaseParametersCaptor.capture());
    Map<String, Object> params = firebaseParametersCaptor.getValue();
    LoanRequestedStatusFirebase loanRequestedStatusFirebase =
        (LoanRequestedStatusFirebase) params.get(LOANREQUESTED_VERIFICATION);
    assertEquals(
        StatusClientVerificationFirebaseEnum.FAILED.name(),
        loanRequestedStatusFirebase.getStatus());
    assertEquals(
        ClientErrorResultsEnum.ERROR_LOAN_SENDING_TO_RISK_ENGINE_SQS.name(),
        loanRequestedStatusFirebase.getDetail());
  }
}
