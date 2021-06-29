package com.lulobank.clients.starter;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.cognitoidp.model.AWSCognitoIdentityProviderException;
import com.amazonaws.services.cognitoidp.model.AdminSetUserPasswordRequest;
import com.amazonaws.services.cognitoidp.model.AdminSetUserPasswordResult;
import com.amazonaws.services.cognitoidp.model.ChangePasswordRequest;
import com.amazonaws.services.cognitoidp.model.ChangePasswordResult;
import com.amazonaws.services.cognitoidp.model.ConfirmForgotPasswordRequest;
import com.amazonaws.services.cognitoidp.model.ConfirmForgotPasswordResult;
import com.amazonaws.services.cognitoidp.model.InvalidPasswordException;
import com.amazonaws.services.cognitoidp.model.UserNotFoundException;
import com.lulobank.clients.services.features.changepassword.model.NewPasswordRequest;
import com.lulobank.clients.services.features.changepassword.model.Password;
import com.lulobank.clients.services.features.recoverpassword.model.ClientWithIdCard;
import com.lulobank.clients.services.features.recoverpassword.model.RecoverPasswordUpdate;
import com.lulobank.clients.services.outboundadapters.model.AttemptEntity;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.utils.ClientHelper;
import com.lulobank.utils.exception.ServiceException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@RunWith(SpringRunner.class)
@ImportAutoConfiguration(RefreshAutoConfiguration.class)
@ActiveProfiles(profiles = "test")
public class PasswordAdapterTest extends AbstractBaseIntegrationTest {

  private static final String URL_VALIDATE_PASSWORD = "/password/validate";
  private static final String URL_VALIDATE_PASSWORD_INTERNAL = "/password/validateInternal";
  private static final String URL_UPDATE_PASSWORD = "/password/update/";
  private static final String URL_RECOVER_PASSWORD = "/recoverPassword/sendEmail";
  private static final String URL_RECOVER_UPDATE_PASSWORD = "/recoverPassword/updatePassword";
  private static final String EMAIL = "mail@mail.com";
  private static final String ID_CARD = "123456789";
  private final String idClient = "774758bf-5890-4b87-89bb-dee9d70ae4c2";
  private final String emailAddress = "seraquesi012345@yopmail.com";
  private final String passwordStr = "Segurisimo5*";
  private Password password;
  private Optional<ClientEntity> clientEntity;
  private ClientEntity client;
  private NewPasswordRequest newPasswordRequest;
  private ResponseEntity response;
  private ClientWithIdCard clientWithIdCard;
  private RecoverPasswordUpdate recoverPasswordUpdate;
  private ConfirmForgotPasswordResult confirmForgotPasswordResult;
  private AttemptEntity attemptEntity;

  @Override
  protected void init() {
    fillPassword();
    fillClientEntity();
    clientEntity = Optional.of(client);
    fillNewPassword();
    clientWithIdCard = new ClientWithIdCard();
    clientWithIdCard.setIdCard(ID_CARD);
    recoverPasswordUpdate = new RecoverPasswordUpdate();
    recoverPasswordUpdate.setEmailAddress("mail@mail.com");
    recoverPasswordUpdate.setNewPassword("NewPass123*");
    recoverPasswordUpdate.setVerificationCode("111111");
    recoverPasswordUpdate.setIdCard("10123456123");
    confirmForgotPasswordResult = new ConfirmForgotPasswordResult();
    attemptEntity = new AttemptEntity(0D, Boolean.FALSE);
  }

  @Test
  public void validatePassword_OK_Response() throws Exception {
    when(clientsRepository.findByIdClientAndEmailAddressAndQualityCode(
            any(String.class), any(String.class), any(String.class)))
        .thenReturn(clientEntity);
    when(loginAttemptsService.getLastDateFailedAttempt(any(String.class)))
        .thenReturn(attemptEntity);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post(URL_VALIDATE_PASSWORD)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerTokenAWS())
                .content(objectMapper.writeValueAsString(password)))
        .andExpect(status().isOk());
  }

  @Test
  public void validatePasswordInternal_OK_Response() throws Exception {
    when(clientsRepository.findByIdClientAndEmailAddressAndQualityCode(
            any(String.class), any(String.class), any(String.class)))
        .thenReturn(clientEntity);
    when(loginAttemptsService.getLastDateFailedAttempt(any(String.class)))
        .thenReturn(attemptEntity);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post(URL_VALIDATE_PASSWORD_INTERNAL)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(password)))
        .andExpect(status().isOk());
  }

  @Test
  public void validatePassword_UNAUTHORIZED_Response() throws Exception {
    clientEntity = Optional.empty();
    when(clientsRepository.findByIdClientAndEmailAddressAndQualityCode(
            any(String.class), any(String.class), any(String.class)))
        .thenReturn(clientEntity);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(URL_VALIDATE_PASSWORD)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerTokenAWS())
                .content(objectMapper.writeValueAsString(password)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void validatePasswordInternal_UNAUTHORIZED_Response() throws Exception {
    clientEntity = Optional.empty();
    when(clientsRepository.findByIdClientAndEmailAddressAndQualityCode(
            any(String.class), any(String.class), any(String.class)))
        .thenReturn(clientEntity);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(URL_VALIDATE_PASSWORD_INTERNAL)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(password)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void validatePassword_BAD_REQUEST_Response() throws Exception {
    password.setPassword(null);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(URL_VALIDATE_PASSWORD)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerTokenAWS())
                .content(objectMapper.writeValueAsString(password)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void validatePasswordInternal_BAD_REQUEST_Response() throws Exception {
    password.setPassword(null);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(URL_VALIDATE_PASSWORD_INTERNAL)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(password)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void updatePassword_NOT_FOUND_Response() throws Exception {
    clientEntity = Optional.empty();
    when(clientsRepository.findByIdClientAndEmailAddress(any(String.class), any(String.class)))
        .thenReturn(clientEntity);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(URL_UPDATE_PASSWORD)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(newPasswordRequest)))
        .andExpect(status().isNotFound());
  }

  @Test
  public void updatePassword_Not_Acceptable_Response() throws Exception {
    newPasswordRequest.setNewPassword(null);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(URL_UPDATE_PASSWORD)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(newPasswordRequest)))
        .andExpect(status().isNotAcceptable());
  }

  @Test
  public void updatePassword_OK_Response() throws Exception {
    clientEntity = Optional.of(client);
    ChangePasswordResult changePasswordResult = new ChangePasswordResult();
    when(clientsRepository.findByIdClientAndEmailAddress(any(String.class), any(String.class)))
        .thenReturn(clientEntity);
    when(clientsRepository.findByIdClient(any(String.class))).thenReturn(clientEntity);
    when(cognitoProperties.getAwsCognitoIdentityProvider()).thenReturn(awsCognitoIdentityProvider);
    when(awsCognitoIdentityProvider.changePassword(any(ChangePasswordRequest.class)))
        .thenReturn(changePasswordResult);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(URL_UPDATE_PASSWORD)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(newPasswordRequest)))
        .andExpect(status().isOk());
  }

  @Test
  public void Should_Return_NOT_FOUND_Since_Recover_Password_Is_Not_Found() throws Exception {
    when(clientsRepository.findByIdCard(any(String.class))).thenReturn(null);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(URL_UPDATE_PASSWORD)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(newPasswordRequest)))
        .andExpect(status().isNotFound());
  }

  @Test
  public void Should_Return_OK_Since_Recover_Password() throws Exception {

    when(clientsRepository.findByIdCard(any(String.class))).thenReturn(client);
    when(retrofitOtpOperations.generateEmailOtp(any(HashMap.class), any(String.class)))
        .thenReturn(false);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(URL_RECOVER_PASSWORD)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(clientWithIdCard)))
        .andExpect(status().isOk());
    verify(clientsRepository, times(1)).findByIdCard(stringArgumentCaptor.capture());
    assertEquals(client.getIdCard(), stringArgumentCaptor.getValue());
  }

  @Test
  public void Should_Return_INTERNAL_SERVER_ERROR_Since_Recover_Password() throws Exception {
    when(clientsRepository.findByIdCard(any(String.class))).thenThrow(new ServiceException(""));
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(URL_RECOVER_PASSWORD)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(clientWithIdCard)))
        .andExpect(status().isInternalServerError());
  }

  @Test
  public void Should_Return_NOT_FOUND_Since_Update_Password_User_Not_Found_Cognito()
      throws Exception {
    when(cognitoProperties.getClientapp_id()).thenReturn("fakeMyPoolId");
    when(cognitoProperties.getAwsCognitoIdentityProvider()).thenReturn(awsCognitoIdentityProvider);
    when(awsCognitoIdentityProvider.confirmForgotPassword(any(ConfirmForgotPasswordRequest.class)))
        .thenThrow(new UserNotFoundException(""));

    mockMvc
        .perform(
            MockMvcRequestBuilders.post(URL_RECOVER_UPDATE_PASSWORD)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(recoverPasswordUpdate)))
        .andExpect(status().isNotFound());
  }

  @Test
  public void Should_Return_BAD_REQUEST_Since_Update_Password_OTP_VALIDATION_FAILURE_Exception()
      throws Exception {
    when(clientsRepository.findByIdCardAndEmailAddress(any(String.class), any(String.class)))
        .thenReturn(clientEntity);
    when(cognitoProperties.getClientapp_id()).thenReturn("fakeMyPoolId");
    when(cognitoProperties.getAwsCognitoIdentityProvider()).thenReturn(awsCognitoIdentityProvider);
    when(retrofitOtpOperations.validateEmailOtp(any(Map.class), anyString(), anyString()))
        .thenReturn(true);
    when(awsCognitoIdentityProvider.adminSetUserPassword(any(AdminSetUserPasswordRequest.class)))
        .thenThrow(new InvalidPasswordException(""));
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(URL_RECOVER_UPDATE_PASSWORD)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(recoverPasswordUpdate)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void Should_Return_NOT_ACCEPTABLE_Since_Update_Password_OTP_VALIDATION_FAILURE()
      throws Exception {
    when(clientsRepository.findByIdCardAndEmailAddress(any(String.class), any(String.class)))
        .thenReturn(clientEntity);
    when(cognitoProperties.getClientapp_id()).thenReturn("fakeMyPoolId");
    when(cognitoProperties.getAwsCognitoIdentityProvider()).thenReturn(awsCognitoIdentityProvider);
    when(retrofitOtpOperations.validateEmailOtp(any(Map.class), anyString(), anyString()))
        .thenReturn(false);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(URL_RECOVER_UPDATE_PASSWORD)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(recoverPasswordUpdate)))
        .andExpect(status().isNotAcceptable());
  }

  @Test
  public void Should_Return_INTERNAL_SERVER_ERROR_Since_Update_Password_COGNITO_ERROR()
      throws Exception {
    when(clientsRepository.findByIdCardAndEmailAddress(any(String.class), any(String.class)))
        .thenReturn(clientEntity);
    when(cognitoProperties.getClientapp_id()).thenReturn("fakeMyPoolId");
    when(cognitoProperties.getAwsCognitoIdentityProvider()).thenReturn(awsCognitoIdentityProvider);
    when(awsCognitoIdentityProvider.adminSetUserPassword(any(AdminSetUserPasswordRequest.class)))
        .thenThrow(new AWSCognitoIdentityProviderException(""));
    when(retrofitOtpOperations.validateEmailOtp(any(Map.class), anyString(), anyString()))
        .thenReturn(true);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(URL_RECOVER_UPDATE_PASSWORD)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(recoverPasswordUpdate)))
        .andExpect(status().isInternalServerError());
  }

  @Test
  public void Should_Returns_BAD_REQUES_Since_Update_Password_Empty_Field() throws Exception {
    recoverPasswordUpdate.setVerificationCode("");
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(URL_RECOVER_UPDATE_PASSWORD)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(recoverPasswordUpdate)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void Should_Return_OK_Since_Update_Password() throws Exception {
    when(clientsRepository.findByIdCardAndEmailAddress(any(String.class), any(String.class)))
        .thenReturn(clientEntity);
    when(retrofitOtpOperations.validateEmailOtp(any(Map.class), anyString(), anyString()))
        .thenReturn(true);
    when(cognitoProperties.getClientapp_id()).thenReturn("fakeMyPoolId");
    when(cognitoProperties.getAwsCognitoIdentityProvider()).thenReturn(awsCognitoIdentityProvider);
    when(awsCognitoIdentityProvider.adminSetUserPassword(any(AdminSetUserPasswordRequest.class)))
        .thenReturn(new AdminSetUserPasswordResult());
    when(clientsRepository.save(any(ClientEntity.class))).thenReturn(client); // captor
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(URL_RECOVER_UPDATE_PASSWORD)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(recoverPasswordUpdate)))
        .andExpect(status().isOk());
    verify(retrofitOtpOperations, times(1))
        .validateEmailOtp(
            any(Map.class), stringArgumentCaptor.capture(), stringArgumentCaptor.capture());
    verify(clientsRepository, times(1)).save(clientEntityArgumentCaptor.capture());
    assertEquals(
        recoverPasswordUpdate.getEmailAddress(), stringArgumentCaptor.getAllValues().get(0));
    assertEquals(
        recoverPasswordUpdate.getVerificationCode(), stringArgumentCaptor.getAllValues().get(1));
    assertEquals(
        ClientHelper.getHashString(recoverPasswordUpdate.getNewPassword()),
        clientEntityArgumentCaptor.getValue().getQualityCode());
  }

  @Test
  public void Should_Return_INTERNAL_SERVER_ERROR_Since_Update_Password_CLIENT_DB_ERROR()
      throws Exception {
    when(clientsRepository.findByIdCardAndEmailAddress(any(String.class), any(String.class)))
        .thenThrow(new SdkClientException(""));
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(URL_RECOVER_UPDATE_PASSWORD)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(recoverPasswordUpdate)))
        .andExpect(status().isInternalServerError());
  }

  @Test
  public void validatePassword_UNAUTHORIZED_Since_User_Not_Blocked() throws Exception {
    clientEntity = Optional.empty();
    when(clientsRepository.findByIdClientAndEmailAddressAndQualityCode(
            any(String.class), any(String.class), any(String.class)))
        .thenReturn(clientEntity);
    when(loginAttemptsService.isBlockedLogin(any(String.class))).thenReturn(true);
    when(loginAttemptsService.getLastDateFailedAttempt(any(String.class)))
        .thenReturn(attemptEntity);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(URL_VALIDATE_PASSWORD_INTERNAL)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(password)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void validatePasswordInternal_UNAUTHORIZED_Since_User_Not_Blocked() throws Exception {
    clientEntity = Optional.empty();
    when(clientsRepository.findByIdClientAndEmailAddressAndQualityCode(
            any(String.class), any(String.class), any(String.class)))
        .thenReturn(clientEntity);
    when(loginAttemptsService.isBlockedLogin(any(String.class))).thenReturn(true);
    when(loginAttemptsService.getLastDateFailedAttempt(any(String.class)))
        .thenReturn(attemptEntity);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(URL_VALIDATE_PASSWORD)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerTokenAWS())
                .content(objectMapper.writeValueAsString(password)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void validatePassword_FORBIDDEN_Response() throws Exception {
    clientEntity = Optional.empty();
    attemptEntity.setPenalty(-1D);
    when(clientsRepository.findByIdClientAndEmailAddressAndQualityCode(
            any(String.class), any(String.class), any(String.class)))
        .thenReturn(clientEntity);
    when(loginAttemptsService.isBlockedLogin(any(String.class))).thenReturn(true);
    when(loginAttemptsService.getLastDateFailedAttempt(any(String.class)))
        .thenReturn(attemptEntity);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(URL_VALIDATE_PASSWORD)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(password)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void validatePassword_INTERNAL_SERVER_ERROR_Response() throws Exception {
    when(clientsRepository.findByIdClientAndEmailAddressAndQualityCode(
            any(String.class), any(String.class), any(String.class)))
        .thenThrow(SdkClientException.class);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(URL_VALIDATE_PASSWORD)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerTokenAWS())
                .content(objectMapper.writeValueAsString(password)))
        .andExpect(status().isInternalServerError());
  }

  @Test
  public void validatePasswordInternal_INTERNAL_SERVER_ERROR_Response() throws Exception {
    when(clientsRepository.findByIdClientAndEmailAddressAndQualityCode(
            any(String.class), any(String.class), any(String.class)))
        .thenThrow(SdkClientException.class);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(URL_VALIDATE_PASSWORD_INTERNAL)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(password)))
        .andExpect(status().isInternalServerError());
  }

  public void fillClientEntity() {
    client = new ClientEntity();
    client.setIdClient(idClient);
    client.setEmailAddress(EMAIL);
    client.setIdCard(ID_CARD);
  }

  public void fillPassword() {
    password = new Password();
    password.setIdClient(idClient);
    password.setEmailAddress(emailAddress);
    password.setPassword(passwordStr);
  }

  public void fillNewPassword() {
    newPasswordRequest = new NewPasswordRequest();
    newPasswordRequest.setIdClient(idClient);
    newPasswordRequest.setAccessToken("qwerty");
    newPasswordRequest.setEmailAddress(emailAddress);
    newPasswordRequest.setOldPassword(passwordStr);
    newPasswordRequest.setNewPassword(passwordStr);
  }
}
