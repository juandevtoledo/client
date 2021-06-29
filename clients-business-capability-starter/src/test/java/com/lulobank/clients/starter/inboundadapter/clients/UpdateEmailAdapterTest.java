package com.lulobank.clients.starter.inboundadapter.clients;

import com.lulobank.biometric.api.validation.otp.dto.OTPTokenValidationResponse;
import com.lulobank.biometric.api.validation.otp.exception.OTPUnauthorizedException;
import com.lulobank.clients.services.features.profilev2.model.UpdateClientEmailRequest;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.starter.AbstractBaseIntegrationTest;
import flexibility.client.models.request.GetClientRequest;
import flexibility.client.models.request.UpdateClientRequest;
import flexibility.client.models.response.GetClientResponse;
import flexibility.client.models.response.UpdateClientResponse;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UpdateEmailAdapterTest extends AbstractBaseIntegrationTest {

  @Value("classpath:mocks/profile/flexibility-get-client.json")
  private Resource flexibilityGetClientResource;

  @Value("classpath:mocks/clients/client-entity.json")
  private Resource clientEntityResource;

  @Captor
  protected ArgumentCaptor<UpdateClientRequest> updateClientRequestCaptor;

  @Captor
  protected ArgumentCaptor<GetClientRequest> getClientRequestCaptor;

  private static final String URL_UPDATE_EMAIL = "/V2/{idClient}/profile/email";

  protected static final String CLIENT_ID = "1106bc49-4a0f-4f52-86ca-1994bb3c26d9";
  private static final String NEW_EMAIL = "newmail@mail.com";
  private static final String KEYCLOAK_ID = "12602b88-a201-4f20-91a0-05bb2f79fb13";
  private static final String CBS_ID = "789456132";

  private ClientEntity clientEntity;
  private UpdateClientEmailRequest updateEmailRequest;
  private GetClientResponse flexibilityGetClient;

  @Override
  protected void init() {
    clientEntity = deserializeResource(clientEntityResource,ClientEntity.class);
    flexibilityGetClient = deserializeResource(flexibilityGetClientResource, GetClientResponse.class);

    updateEmailRequest = new UpdateClientEmailRequest();
    updateEmailRequest.setIdClient(CLIENT_ID);
    updateEmailRequest.setNewEmail(NEW_EMAIL);
  }

  @Test
  public void should_return_ok_at_update_email() throws Exception {
    when(otpJwtValidator.validateWithUserCredentials(any(),any())).thenReturn(new OTPTokenValidationResponse(true));
    when(clientsRepository.findByIdClient(any(String.class))).thenReturn(Optional.of(clientEntity));
    when(clientsRepository.save(clientEntityCaptor.capture())).thenReturn(clientEntity);
    when(flexibilitySdk.getClientById(getClientRequestCaptor.capture())).thenReturn(flexibilityGetClient);
    when(flexibilitySdk.updateClient(updateClientRequestCaptor.capture())).thenReturn(new UpdateClientResponse());

    mockMvc.perform(
            MockMvcRequestBuilders.put(URL_UPDATE_EMAIL, ID_CLIENT)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .header("otp-token" , "1111:54ds5fa4df6d")
                .content(objectMapper.writeValueAsString(updateEmailRequest)))
        .andExpect(status().isOk());

    verify(identityProviderService, times(1)).updateUserEmail(stringArgumentCaptor.capture(), stringArgumentCaptor.capture());
    verify(flexibilitySdk, times(1)).updateClient(any(UpdateClientRequest.class));

    assertEquals(NEW_EMAIL, clientEntityCaptor.getValue().getEmailAddress());
    assertEquals(NEW_EMAIL, updateClientRequestCaptor.getValue().getEmail());
    assertEquals(NEW_EMAIL, stringArgumentCaptor.getAllValues().get(1));
    assertEquals(CBS_ID, getClientRequestCaptor.getValue().getClientId());
    assertEquals(KEYCLOAK_ID, stringArgumentCaptor.getAllValues().get(0));
  }

  @Test
  public void should_return_invalid_request_at_update_email_with_existent_email() throws Exception {
    when(otpJwtValidator.validateWithUserCredentials(any(),any())).thenReturn(new OTPTokenValidationResponse(true));
    when(clientsRepository.findByIdClient(any(String.class))).thenReturn(Optional.of(clientEntity));
    when(clientsRepository.findByEmailAddress(any(String.class))).thenReturn(clientEntity);

    mockMvc
        .perform(
            MockMvcRequestBuilders.put(URL_UPDATE_EMAIL, ID_CLIENT)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .header("otp-token" , "1111:54ds5fa4df6d")
                .content(objectMapper.writeValueAsString(updateEmailRequest)))
        .andExpect(status().isPreconditionFailed());

    verify(clientsRepository).findByEmailAddress(stringArgumentCaptor.capture());

    assertEquals(NEW_EMAIL, stringArgumentCaptor.getValue());
  }

  @Test
  public void should_return_bad_request_at_update_email_with_empty_request() throws Exception {
    updateEmailRequest.setNewEmail(null);
    mockMvc
        .perform(
            MockMvcRequestBuilders.put(URL_UPDATE_EMAIL, ID_CLIENT)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(updateEmailRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void should_return_server_error_at_update_email_with() throws Exception {
    when(otpJwtValidator.validateWithUserCredentials(any(),any())).thenReturn(new OTPTokenValidationResponse(true));
    when(clientsRepository.findByIdClient(any(String.class))).thenReturn(Optional.of(clientEntity));
    when(clientsRepository.findByEmailAddress(any(String.class))).thenThrow(new RuntimeException());

    mockMvc
        .perform(
            MockMvcRequestBuilders.put(URL_UPDATE_EMAIL, ID_CLIENT)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .header("otp-token" , "1111:54ds5fa4df6d")
                .content(objectMapper.writeValueAsString(updateEmailRequest)))
        .andExpect(status().isInternalServerError());
  }

  @Test
  public void should_return_forbidden_at_update_email_without_otp_header() throws Exception {

    mockMvc
            .perform(
                    MockMvcRequestBuilders.put(URL_UPDATE_EMAIL, ID_CLIENT)
                            .contentType(CONTENT_TYPE_JSON)
                            .with(bearerToken())
                            .content(objectMapper.writeValueAsString(updateEmailRequest)))
            .andExpect(status().isNotAcceptable());
  }

  @Test
  public void should_return_forbidden_at_update_email_invalid_otp() throws Exception {
    when(otpJwtValidator.validateWithUserCredentials(any(),any())).thenThrow(new OTPUnauthorizedException(""));
    mockMvc
            .perform(
                    MockMvcRequestBuilders.put(URL_UPDATE_EMAIL, ID_CLIENT)
                            .contentType(CONTENT_TYPE_JSON)
                            .with(bearerToken())
                            .header("otp-token" , "1111:54ds5fa4df6d")
                            .content(objectMapper.writeValueAsString(updateEmailRequest)))
            .andExpect(status().isNotAcceptable());
  }
}
