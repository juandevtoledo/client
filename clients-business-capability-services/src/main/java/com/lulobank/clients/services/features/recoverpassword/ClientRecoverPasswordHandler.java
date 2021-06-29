package com.lulobank.clients.services.features.recoverpassword;

import com.lulobank.clients.services.features.recoverpassword.model.ClientWithIdCard;
import com.lulobank.clients.services.features.recoverpassword.model.RecoverPasswordEmailClient;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.clients.services.utils.ClientErrorResultsEnum;
import com.lulobank.core.Handler;
import com.lulobank.core.Response;
import com.lulobank.core.utils.ValidatorUtils;
import com.lulobank.utils.exception.ServiceException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class ClientRecoverPasswordHandler
    implements Handler<Response<RecoverPasswordEmailClient>, ClientWithIdCard> {
  private static final Logger LOGGER = LoggerFactory.getLogger(ClientRecoverPasswordHandler.class);
  private ClientsRepository clientsRepository;

  public ClientRecoverPasswordHandler(ClientsRepository clientsRepository) {
    this.clientsRepository = clientsRepository;
  }

  @Override
  public Response handle(ClientWithIdCard clientWithIdCard) {
    try {
      ClientEntity clientEntity =
          Optional.ofNullable(clientsRepository.findByIdCard(clientWithIdCard.getIdCard()))
              .orElseThrow(
                  () ->
                      new ServiceException(
                          HttpStatus.NOT_FOUND.value(),
                          ClientErrorResultsEnum.CLIENT_NOT_FOUND_IN_DB.name()));

      RecoverPasswordEmailClient recoverPasswordEmailClient =
          new RecoverPasswordEmailClient(clientEntity.getEmailAddress());
      return new Response<>(recoverPasswordEmailClient);
    } catch (ServiceException e) {
      LOGGER.error(e.getMessage(), e);
      return new Response<>(
          ValidatorUtils.getListValidations(e.getMessage(), String.valueOf(e.getCode())));
    }
  }
}
