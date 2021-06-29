package com.lulobank.clients.services.features.infoclient;

import com.amazonaws.SdkClientException;
import com.lulobank.clients.sdk.operations.dto.ClientInformationByIdCard;
import com.lulobank.clients.sdk.operations.dto.GetClientInformationByIdCard;
import com.lulobank.clients.services.outboundadapters.ClientsOutboundAdapter;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.utils.ClientErrorResultsEnum;
import com.lulobank.clients.services.utils.ConverterObjectUtils;
import com.lulobank.clients.services.utils.LogMessages;
import com.lulobank.core.Handler;
import com.lulobank.core.Response;
import com.lulobank.core.utils.ValidatorUtils;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class GetClientInfoByIdCardHandler
    implements Handler<Response<ClientInformationByIdCard>, GetClientInformationByIdCard> {
  private static final Logger LOGGER = LoggerFactory.getLogger(GetClientInfoByIdCardHandler.class);

  private ClientsOutboundAdapter clientsOutboundAdapter;

  public GetClientInfoByIdCardHandler(ClientsOutboundAdapter clientsOutboundAdapter) {
    this.clientsOutboundAdapter = clientsOutboundAdapter;
  }

  @Override
  public Response<ClientInformationByIdCard> handle(
      GetClientInformationByIdCard getClientInformationByIdCard) {
    try {
      ClientEntity clientEntity =
          clientsOutboundAdapter
              .getClientsRepository()
              .findByIdCard(getClientInformationByIdCard.getIdCard());

      if (Objects.nonNull(clientEntity)) {
        return new Response<>(
            ConverterObjectUtils.createClientByIdCardTransactionResponseFromClientEntity(
                clientEntity));
      }

      return new Response<>(
          ValidatorUtils.getListValidations(
              ClientErrorResultsEnum.CLIENT_NOT_FOUND_IN_DB.name(),
              String.valueOf(HttpStatus.NOT_FOUND.value())));
    } catch (SdkClientException e) {
      LOGGER.error(LogMessages.DYNAMO_ERROR_EXCEPTION.getMessage(), e.getMessage(), e);
      return new Response<>(
          ValidatorUtils.getListValidations(
              ClientErrorResultsEnum.INTERNAL_SERVER_ERROR.name(),
              String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())));
    }
  }
}
