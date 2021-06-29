package com.lulobank.clients.starter.inboundadapter.clients;

import com.amazonaws.SdkClientException;
import com.lulobank.biometric.api.validation.otp.dto.OTPTokenValidationResponse;
import com.lulobank.biometric.api.validation.otp.exception.OTPUnauthorizedException;
import com.lulobank.clients.sdk.operations.dto.UpdateClientAddressRequest;
import com.lulobank.clients.services.domain.StateBlackList;
import com.lulobank.clients.services.exception.CoreBankingException;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.starter.AbstractBaseIntegrationTest;
import flexibility.client.connector.ProviderException;
import io.vavr.control.Try;
import org.junit.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ClientUpdateAddressAdapterTest extends AbstractBaseIntegrationTest {

  private static final String URL_UPDATE_ADDRESS = "/{idClient}/profile";

  protected static final String CLIENT_ID = "1106bc49-4a0f-4f52-86ca-1994bb3c26d9";
  private static final String KEYCLOAK_ID = "12602b88-a201-4f20-91a0-05bb2f79fb13";
  private static final String NEW_EMAIL = "newmail@mail.com";
  private static final String CARD_ID = "12345678";
  private static final String NAME = "some-name";
  private static final String LAST_NAME = "some-lastname";
  private static final int PHONE_PREFIX = 57;
  private static final String PHONE_NUMBER = "3168906733";
  private static final String ADDRESS = "some-address";
  private static final String ADDRESS_PREFIX = "some-prefix";
  private static final String CITY = "some-city";
  private static final String CITY_ID = "1";
  private static final String DEPARTMENT = "some-department";
  private static final String DEPARTMENT_ID = "1";

  private ClientEntity clientEntity;
  private UpdateClientAddressRequest updateClientProfileRequest;

  @Override
  protected void init() {
    clientEntity = new ClientEntity();
    clientEntity.setIdClient(CLIENT_ID);
    clientEntity.setIdCard(CARD_ID);
    clientEntity.setName(NAME);
    clientEntity.setLastName(LAST_NAME);
    clientEntity.setAddress(ADDRESS);
    clientEntity.setPhonePrefix(PHONE_PREFIX);
    clientEntity.setPhoneNumber(PHONE_NUMBER);
    clientEntity.setEmailAddress(NEW_EMAIL);
    clientEntity.setEmailVerified(Boolean.TRUE);
    clientEntity.setIdKeycloak(KEYCLOAK_ID);
    clientEntity.setBlackListState(StateBlackList.NON_BLACKLISTED.name());

    updateClientProfileRequest = new UpdateClientAddressRequest();
    updateClientProfileRequest.setAddressPrefix(ADDRESS_PREFIX);
    updateClientProfileRequest.setAddress(ADDRESS);
    updateClientProfileRequest.setDepartmentId(DEPARTMENT_ID);
    updateClientProfileRequest.setDepartment(DEPARTMENT);
    updateClientProfileRequest.setCity(CITY);
    updateClientProfileRequest.setCityId(CITY_ID);

  }

  @Test
  public void should_return_forbidden_otp_header_invalid_update_address() throws Exception {
    when(otpJwtValidator.validateWithUserCredentials(any(),any())).thenThrow(new OTPUnauthorizedException(""));
    mockMvc
            .perform(
                    MockMvcRequestBuilders.put(URL_UPDATE_ADDRESS, ID_CLIENT)
                            .contentType(CONTENT_TYPE_JSON)
                            .with(bearerToken())
                            .header("otp-token" , "1111:54ds5fa4df6d")
                            .content(objectMapper.writeValueAsString(updateClientProfileRequest)))
            .andExpect(status().isNotAcceptable());
  }

  @Test
  public void should_return_ok_update_address() throws Exception {
    when(otpJwtValidator.validateWithUserCredentials(any(),any())).thenReturn(new OTPTokenValidationResponse(true));
    when(updateClientAddressUseCase.execute(any())).thenReturn(Try.success(true));

    mockMvc
            .perform(
                    MockMvcRequestBuilders.put(URL_UPDATE_ADDRESS, ID_CLIENT)
                            .contentType(CONTENT_TYPE_JSON)
                            .with(bearerToken())
                            .header("otp-token" , "1111:54ds5fa4df6d")
                            .content(objectMapper.writeValueAsString(updateClientProfileRequest)))
            .andExpect(status().isOk());

  }

  @Test
  public void should_return_precondition_failed_update_address() throws Exception {
    when(otpJwtValidator.validateWithUserCredentials(any(),any())).thenReturn(new OTPTokenValidationResponse(true));
    when(updateClientAddressUseCase.execute(any())).thenReturn(Try.failure(new CoreBankingException("",new ProviderException("",""))));

    mockMvc
            .perform(
                    MockMvcRequestBuilders.put(URL_UPDATE_ADDRESS, ID_CLIENT)
                            .contentType(CONTENT_TYPE_JSON)
                            .with(bearerToken())
                            .header("otp-token" , "1111:54ds5fa4df6d")
                            .content(objectMapper.writeValueAsString(updateClientProfileRequest)))
            .andExpect(status().isPreconditionFailed());

  }

  @Test
  public void should_return_internal_error_update_address() throws Exception {
    when(otpJwtValidator.validateWithUserCredentials(any(),any())).thenReturn(new OTPTokenValidationResponse(true));
    when(updateClientAddressUseCase.execute(any())).thenReturn(Try.failure(new SdkClientException("")));

    mockMvc
            .perform(
                    MockMvcRequestBuilders.put(URL_UPDATE_ADDRESS, ID_CLIENT)
                            .contentType(CONTENT_TYPE_JSON)
                            .with(bearerToken())
                            .header("otp-token" , "1111:54ds5fa4df6d")
                            .content(objectMapper.writeValueAsString(updateClientProfileRequest)))
            .andExpect(status().isInternalServerError());

  }

  @Test
  public void should_return_forbidden_otp_header_missing_update_address() throws Exception {
    mockMvc
            .perform(
                    MockMvcRequestBuilders.put(URL_UPDATE_ADDRESS, ID_CLIENT)
                            .contentType(CONTENT_TYPE_JSON)
                            .with(bearerToken())
                            .content(objectMapper.writeValueAsString(updateClientProfileRequest)))
            .andExpect(status().isNotAcceptable());

  }

  @Test
  public void should_return_bad_request_update_address() throws Exception {
    when(otpJwtValidator.validateWithUserCredentials(any(),any())).thenReturn(new OTPTokenValidationResponse(true));
    updateClientProfileRequest.setAddress(null);
    mockMvc
            .perform(
                    MockMvcRequestBuilders.put(URL_UPDATE_ADDRESS, ID_CLIENT)
                            .contentType(CONTENT_TYPE_JSON)
                            .with(bearerToken())
                            .header("otp-token" , "1111:54ds5fa4df6d")
                            .content(objectMapper.writeValueAsString(updateClientProfileRequest)))
            .andExpect(status().isBadRequest());

  }
}