package com.lulobank.clients.starter.inboundadapter.onboarding;

import com.google.gson.Gson;
import com.lulobank.clients.services.exception.DigitalEvidenceException;
import com.lulobank.clients.services.exception.TimestampDigitalEvidenceException;
import com.lulobank.clients.services.exception.ValidateRequestException;
import com.lulobank.clients.services.features.initialclient.model.CreateInitialClient;
import com.lulobank.clients.services.features.initialclient.model.InitialClientCreated;
import com.lulobank.clients.starter.AbstractBaseIntegrationTest;
import com.lulobank.clients.starter.inboundadapter.dto.CreateInitialClientRequest;
import io.vavr.control.Try;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.lulobank.clients.services.utils.ClientsErrorResponse.DIGITAL_EVIDENCE_ERROR;
import static com.lulobank.clients.services.utils.ClientsErrorResponse.TIMESTAMP_ERROR;
import static com.lulobank.clients.services.utils.ClientsErrorResponse.UNPROCESSABLE_REQUEST;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InitialClientAdapterTest extends AbstractBaseIntegrationTest {

  @Value("classpath:mocks/clients/initial-client-request.json")
  private Resource initialClientRequest;

  @Value("classpath:mocks/clients/initial-client-response.json")
  private Resource initialClientResponse;

  @Value("classpath:mocks/clients/client-entity.json")
  private Resource clientEntityResource;

  @Captor
  private ArgumentCaptor<CreateInitialClient> initialClientCaptor;

  private static final String INITIAL_CLIENT_URL = "/V2/onboarding/initialClient";

  private CreateInitialClientRequest createInitialClientRequest;

  @Override
  protected void init() {
    createInitialClientRequest = deserializeResource(initialClientRequest, CreateInitialClientRequest.class);
  }

  @Test
  public void should_return_ok_at_creating_initial_client() throws Exception {
    InitialClientCreated initialClientCreated = deserializeResource(initialClientResponse, InitialClientCreated.class);

    when(initialClientUseCase.execute(initialClientCaptor.capture())).thenReturn(Try.success(initialClientCreated));

    String requestContent = new Gson().toJson(createInitialClientRequest);
    mockMvc.perform(MockMvcRequestBuilders
        .post(INITIAL_CLIENT_URL)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
            .header("firebase-id","")
            .with(bearerToken())
        .content(requestContent)
        .accept(MediaType.APPLICATION_JSON)
        .characterEncoding("utf-8"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("content.idClient", notNullValue()))
        .andExpect(jsonPath("content.tokensSignUp.accessToken", notNullValue()));

    CreateInitialClient createInitialClient = initialClientCaptor.getValue();
    assertEquals(createInitialClientRequest.getPassword(), createInitialClient.getPassword());
    assertEquals(createInitialClientRequest.getEmailCreateClientRequest().getAddress(), createInitialClient.getEmailCreateClientRequest().getAddress());
    assertEquals(createInitialClientRequest.getDocumentAcceptancesTimestamp().toString(), createInitialClient.getDocumentAcceptancesTimestamp());
  }

  @Test
  public void should_return_bad_request() throws Exception {
    when(initialClientUseCase.execute(any(CreateInitialClient.class)))
        .thenReturn(Try.failure(new ValidateRequestException("Bad request", HttpStatus.BAD_REQUEST.value())));

    String requestContent = new Gson().toJson(createInitialClientRequest);
    mockMvc.perform(MockMvcRequestBuilders
        .post(INITIAL_CLIENT_URL)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
            .header("firebase-id","")
            .with(bearerToken())
        .content(requestContent)
        .accept(MediaType.APPLICATION_JSON)
        .characterEncoding("utf-8"))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void should_return_internal_error_as_digital_evidence_fails() throws Exception {
    when(initialClientUseCase.execute(any(CreateInitialClient.class)))
        .thenReturn(Try.failure(new DigitalEvidenceException("Digital evidence error")));

    String requestContent = new Gson().toJson(createInitialClientRequest);
    mockMvc.perform(MockMvcRequestBuilders
        .post(INITIAL_CLIENT_URL)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
            .header("firebase-id","")
            .with(bearerToken())
        .content(requestContent)
        .accept(MediaType.APPLICATION_JSON)
        .characterEncoding("utf-8"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("code", is(DIGITAL_EVIDENCE_ERROR.code())))
        .andExpect(jsonPath("failure", is("500")))
        .andExpect(jsonPath("detail", is("U")));
  }

  @Test
  public void should_return_internal_error_as_timestamp_error() throws Exception {
    when(initialClientUseCase.execute(any(CreateInitialClient.class)))
        .thenReturn(Try.failure(new TimestampDigitalEvidenceException("Timestamp error")));

    String requestContent = new Gson().toJson(createInitialClientRequest);
    mockMvc.perform(MockMvcRequestBuilders
        .post(INITIAL_CLIENT_URL)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
            .header("firebase-id","")
        .with(bearerToken())
        .content(requestContent)
        .accept(MediaType.APPLICATION_JSON)
        .characterEncoding("utf-8"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("code", is(TIMESTAMP_ERROR.code())))
        .andExpect(jsonPath("failure", is(TIMESTAMP_ERROR.name())))
        .andExpect(jsonPath("detail", notNullValue()));
  }

  @Test
  public void should_return_internal_error_as_unexpected_error() throws Exception {
    when(initialClientUseCase.execute(any(CreateInitialClient.class)))
        .thenReturn(Try.failure(new RuntimeException("Unknown error")));

    String requestContent = new Gson().toJson(createInitialClientRequest);
    mockMvc.perform(MockMvcRequestBuilders
        .post(INITIAL_CLIENT_URL)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
            .header("firebase-id","")
            .with(bearerToken())
        .content(requestContent)
        .accept(MediaType.APPLICATION_JSON)
        .characterEncoding("utf-8"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("code", is(UNPROCESSABLE_REQUEST.code())))
        .andExpect(jsonPath("failure", is(UNPROCESSABLE_REQUEST.name())))
        .andExpect(jsonPath("detail", notNullValue()));
  }

}
