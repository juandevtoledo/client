package com.lulobank.clients.services.features.profile;

import com.amazonaws.SdkClientException;
import com.lulobank.clients.sdk.operations.dto.VerifyEmailResponse;
import com.lulobank.clients.services.features.profile.model.VerifyEmailRequest;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.clients.services.utils.ClientErrorResultsEnum;
import com.lulobank.clients.services.utils.LogMessages;
import com.lulobank.core.Handler;
import com.lulobank.core.Response;
import com.lulobank.core.utils.ValidatorUtils;
import java.util.Objects;

import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class VerifyEmailHandler
    implements Handler<Response<VerifyEmailResponse>, VerifyEmailRequest> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(VerifyEmailHandler.class);
  private ClientsRepository clientsRepository;

  public VerifyEmailHandler(ClientsRepository clientsRepository) {
    this.clientsRepository = clientsRepository;
  }

  @Override
  public Response<VerifyEmailResponse> handle(final VerifyEmailRequest verifyEmailRequest) {
    try {
      ClientEntity clientEntity =
          clientsRepository.findByEmailAddress(verifyEmailRequest.getEmail());
      if (Objects.nonNull(clientEntity)) {
        clientEntity.setEmailVerified(true);
        clientsRepository.save(clientEntity);
        LOGGER.info(
            LogMessages.CLIENT_EMAIL_VERIFIED.getMessage(),
            Encode.forJava(clientEntity.getIdClient()),
            Encode.forJava(clientEntity.getEmailAddress()));
        return new Response<>(
            new VerifyEmailResponse(
                clientEntity.getEmailAddress(), clientEntity.getEmailVerified()));
      } else {
        LOGGER.error(
            LogMessages.CLIENT_NOT_FOUND_IN_DB_EXCEPTION.getMessage(),
            Encode.forJava(verifyEmailRequest.getEmail()));
        return new Response<>(
            ValidatorUtils.getListValidations(
                ClientErrorResultsEnum.CLIENT_NOT_FOUND_IN_DB.name(),
                String.valueOf(HttpStatus.NOT_FOUND.value())));
      }
    } catch (SdkClientException e) {
      LOGGER.error(LogMessages.DYNAMO_ERROR_EXCEPTION.getMessage(), e.getMessage(), e);
      return new Response<>(
          ValidatorUtils.getListValidations(
              ClientErrorResultsEnum.INTERNAL_SERVER_ERROR.name(),
              String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())));
    }
  }
}
