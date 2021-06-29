package com.lulobank.clients.services.features.onboardingclients;

import com.amazonaws.SdkClientException;
import com.lulobank.clients.sdk.operations.dto.onboardingclients.ClientInformationByIdClient;
import com.lulobank.clients.sdk.operations.dto.onboardingclients.GetClientInformationByIdClient;
import com.lulobank.clients.services.exception.ClientNotFoundException;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.clients.services.utils.AccountStatusEnum;
import com.lulobank.clients.services.utils.ClientErrorResultsEnum;
import com.lulobank.clients.services.utils.ConverterObjectUtils;
import com.lulobank.clients.services.utils.LogMessages;
import com.lulobank.core.Handler;
import com.lulobank.core.Response;
import com.lulobank.core.validations.ValidationResult;
import com.lulobank.utils.exception.ServiceException;
import flexibility.client.connector.ProviderException;
import flexibility.client.models.request.GetAccountRequest;
import flexibility.client.models.response.GetAccountResponse;
import flexibility.client.sdk.FlexibilitySdk;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetClientInformationByIdClientHandler
    implements Handler<Response<ClientInformationByIdClient>, GetClientInformationByIdClient> {
  private static final Logger logger =
      LoggerFactory.getLogger(GetClientInformationByIdClientHandler.class);
  
  private static final String UUID_REGX = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$";
  private ClientsRepository repository;
  private FlexibilitySdk flexibilitySdk;

  public GetClientInformationByIdClientHandler(
      ClientsRepository repository, FlexibilitySdk flexibilitySdk) {
    this.repository = repository;
    this.flexibilitySdk = flexibilitySdk;
  }

  @Override
  public Response<ClientInformationByIdClient> handle(
      GetClientInformationByIdClient getClientInformationByIdClient) {
    Response response = null;
    try {
      List<GetAccountResponse> clientAccounts = new ArrayList<>();
      ClientEntity clientEntity =
          repository
              .findByIdClient(getClientInformationByIdClient.getIdClient())
              .orElseThrow(ClientNotFoundException::new);
      
      
      if (isValidIdCbs(clientEntity.getIdCbs())) {
        try {
          GetAccountRequest getAccountRequest = new GetAccountRequest();
          getAccountRequest.setClientId(clientEntity.getIdCbs());
          Optional.ofNullable(flexibilitySdk.getAccountsByClientId(getAccountRequest))
              .ifPresent(
                  accountResponses ->
                      clientAccounts.addAll(
                          accountResponses.stream()
                              .filter(isActiveOrApprovedOrLockedAccount)
                              .collect(Collectors.toList())));
        } catch (ProviderException ex) {
          logger.error(LogMessages.ERROR_CORE_BANKING.name(), ex.getMessage(), ex);
        } catch (ServiceException ex) {
          logger.error(LogMessages.SERVICE_EXCEPTION.name(), ex.getMessage(), ex);
        }
      }
      response =
          new Response<>(
              ConverterObjectUtils.createClientInformationByIdCardFromClientEntity(
                  clientEntity, clientAccounts));
    } catch (SdkClientException e) {
      response =
          new Response<>(
              getValidationError(
                  new ValidationResult(
                      ClientErrorResultsEnum.CLIENT_DB_ERROR.name(),
                      ClientErrorResultsEnum.CLIENT_DB_ERROR.name())));
    }
    return response;
  }
  
  private boolean isValidIdCbs(String idCbs) {
	  return Optional.ofNullable(idCbs)
	  	.filter(value -> !Pattern.matches(UUID_REGX, value))
	  	.isPresent();
  }

  private List<ValidationResult> getValidationError(ValidationResult clientNotFound) {
    List<ValidationResult> errors = new ArrayList<>();
    errors.add(clientNotFound);
    return errors;
  }

  private Predicate<GetAccountResponse> isActiveOrApprovedOrLockedAccount =
      account ->
          AccountStatusEnum.ACTIVE.name().equals(account.getState())
              || AccountStatusEnum.APPROVED.name().equals(account.getState())
              || AccountStatusEnum.LOCKED.name().equals(account.getState());
}
