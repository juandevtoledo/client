package com.lulobank.clients.services.features.recoverpassword.action;

import static com.lulobank.core.utils.ValidatorUtils.getListValidations;

import com.lulobank.clients.services.features.recoverpassword.model.ClientWithIdCard;
import com.lulobank.clients.services.features.recoverpassword.model.RecoverPasswordEmailClient;
import com.lulobank.clients.services.outboundadapters.ClientsOutboundAdapter;
import com.lulobank.clients.services.utils.ClientErrorResultsEnum;
import com.lulobank.core.Response;
import com.lulobank.core.actions.Action;
import com.lulobank.utils.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class RecoverPasswordRetrofitClientAction
    implements Action<Response<RecoverPasswordEmailClient>, ClientWithIdCard> {

  private ClientsOutboundAdapter clientsOutboundAdapter;

  private static final Logger LOGGER =
      LoggerFactory.getLogger(RecoverPasswordRetrofitClientAction.class);

  private static final String ERROR_OTP = "Error sending OTP";

  public RecoverPasswordRetrofitClientAction(ClientsOutboundAdapter clientsOutboundAdapter) {
    this.clientsOutboundAdapter = clientsOutboundAdapter;
  }

  @Override
  public void run(
      Response<RecoverPasswordEmailClient> recoveredPasswordClient,
      ClientWithIdCard clientWithIdCard) {
    try {
      clientsOutboundAdapter
          .getRetrofitOtpOperations()
          .generateEmailOtp(
              clientWithIdCard.getAuthorizationHeader(),
              recoveredPasswordClient.getContent().getEmailAddress());
    } catch (ServiceException e) {
      LOGGER.error(ClientErrorResultsEnum.RETROFIT_SERVICE_EXCEPTION.name(), e);
      recoveredPasswordClient
          .getErrors()
          .add(
              getListValidations(
                      ERROR_OTP, String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                  .get(0));
    }
  }
}
