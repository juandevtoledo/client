package com.lulobank.clients.starter;

import static com.lulobank.clients.services.utils.ProductTypeEnum.SAVING_ACCOUNT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.amazonaws.SdkClientException;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.model.OnBoardingStatus;
import com.lulobank.core.Response;
import com.lulobank.savingsaccounts.sdk.dto.createsavingsaccount.CreateSavingsAccountRequest;
import com.lulobank.savingsaccounts.sdk.dto.createsavingsaccount.CreateSavingsAccountResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Optional;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class OnboardingChangeProductSavingsTest extends AbstractBaseIntegrationTest {
  private static final String TESTED_URL = "/onboarding/{idClient}/change/product/savings";
  private ClientEntity clientEntityFound;

  @Value("classpath:mocks/onboardingclients/ClientOnboardingClientNotFoundResponse.json")
  private Resource responseClientOnboardingNotFound;

  @Override
  protected void init() {
    clientEntityFound = new ClientEntity();
    clientEntityFound.setIdClient("1234-5432");
    clientEntityFound.setDateOfIssue(LocalDate.now());
    clientEntityFound.setIdCard("12345678");
    clientEntityFound.setOnBoardingStatus(new OnBoardingStatus("1", SAVING_ACCOUNT.name()));
    CreateSavingsAccountResponse createSavingsAccountResponse = new CreateSavingsAccountResponse();
    createSavingsAccountResponse.setIdCbs("523423452ewafqw2313242");
    when(savingsAccount.createSavingsAccount(
            any(HashMap.class), any(CreateSavingsAccountRequest.class)))
        .thenReturn(new Response(createSavingsAccountResponse));
    when(databaseReference.child(anyString())).thenReturn(databaseReference);
  }

  @Test
  public void shouldReturnOkOnChangeProductSaving() throws Exception {
    when(clientsRepository.findByIdClient(any(String.class)))
        .thenReturn(Optional.of(clientEntityFound));
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(TESTED_URL, ID_CLIENT)
                .accept(MediaType.APPLICATION_JSON)
                .with(bearerToken()))
        .andExpect(status().isOk());
    Mockito.verify(clientsRepository, times(1)).save(clientEntityCaptor.capture());
  }

  @Test
  public void shouldReturnNotFoundClientNotExistInDBRequestedLoan() throws Exception {
    when(clientsRepository.findByIdClient(any(String.class))).thenReturn(Optional.empty());
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
                        responseClientOnboardingNotFound.getFile(), StandardCharsets.UTF_8)));
  }

  @Test
  public void shouldReturnNotFoundErrorSavingInDBRequestedLoan() throws Exception {
    when(clientsRepository.findByIdClient(any(String.class)))
        .thenReturn(Optional.of(clientEntityFound));
    when(clientsRepository.save(any(ClientEntity.class)))
        .thenThrow(new SdkClientException("ERROR SAVING"));
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(TESTED_URL, ID_CLIENT)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken()))
        .andExpect(status().isInternalServerError());
  }
}
