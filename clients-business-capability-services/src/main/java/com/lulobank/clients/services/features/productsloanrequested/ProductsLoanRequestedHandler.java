package com.lulobank.clients.services.features.productsloanrequested;

import static com.lulobank.clients.services.utils.ClientErrorResultsEnum.CLIENT_NOT_FOUND_IN_DB;
import static com.lulobank.clients.services.utils.ClientErrorResultsEnum.ERROR_LOAN_SENDING_TO_RISK_ENGINE_SQS;
import static com.lulobank.clients.services.utils.ClientErrorResultsEnum.CLIENT_ALREADY_HAS_AN_ACTIVE_LOAN;
import static com.lulobank.clients.services.utils.ClientHelper.getFirebaseFailParamsFromHomeCredit;
import static com.lulobank.clients.services.utils.ClientHelper.notifyLoanFromHomeFailed;
import static com.lulobank.clients.services.utils.ClientHelper.notifyLoanRequestedCreated;
import static com.lulobank.clients.services.utils.ClientHelper.notifyRiskEngine;
import static com.lulobank.clients.services.utils.LogMessages.ERROR_SENDING_RISK_ENGINE_EVENT;
import static java.lang.Boolean.TRUE;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;

import com.lulobank.clients.services.exception.ClientNotFoundException;
import com.lulobank.clients.services.outboundadapters.ClientsOutboundAdapter;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.model.LoanRequested;
import com.lulobank.clients.services.utils.LoanRequestedStatus;
import com.lulobank.clients.v3.adapters.port.out.credits.CreditsService;
import com.lulobank.core.Handler;
import com.lulobank.core.Response;
import com.lulobank.core.utils.ValidatorUtils;
import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.MessagingException;

public class ProductsLoanRequestedHandler
    implements Handler<Response, ProductsLoanRequestedWithClient> {

  private static final Logger logger = LoggerFactory.getLogger(ProductsLoanRequestedHandler.class);

  private ClientsOutboundAdapter clientsOutboundAdapter;
  
  private final CreditsService creditsService;

  public ProductsLoanRequestedHandler(ClientsOutboundAdapter clientsOutboundAdapter, CreditsService creditsService) {
    this.clientsOutboundAdapter = clientsOutboundAdapter;
    this.creditsService = creditsService;
  }

  @Override
  public Response handle(ProductsLoanRequestedWithClient productsLoanRequestedWithClient) {
	  
	if(creditsService.getActiveLoan(productsLoanRequestedWithClient.getIdClient(), productsLoanRequestedWithClient.getHeaders().toSingleValueMap())
	  .isRight()) {
		return new Response(
	              ValidatorUtils.getListValidations(
	            		  CLIENT_ALREADY_HAS_AN_ACTIVE_LOAN.name(),
	                      String.valueOf(NOT_ACCEPTABLE.value())));
	}
	
    Response response = null;
    ClientEntity clientEntity =
        clientsOutboundAdapter
            .getClientsRepository()
            .findByIdClient(productsLoanRequestedWithClient.getIdClient())
            .orElseThrow(
                () ->
                    new ClientNotFoundException(
                        CLIENT_NOT_FOUND_IN_DB.name(),
                        productsLoanRequestedWithClient.getIdClient()));

    clientEntity.setLoanRequested(new LoanRequested(LoanRequestedStatus.IN_PROGRESS.name()));
    try {
      notifyRiskEngine
          .andThen(notifyLoanRequestedCreated)
          .accept(clientEntity, clientsOutboundAdapter);
      clientsOutboundAdapter.getClientsRepository().save(clientEntity);
      response = new Response(TRUE);
    } catch (MessagingException e) {
      logger.error(ERROR_SENDING_RISK_ENGINE_EVENT.getMessage(), Encode.forJava(clientEntity.getIdClient()), e);
      notifyLoanFromHomeFailed.accept(
          clientsOutboundAdapter,
          getFirebaseFailParamsFromHomeCredit(
              clientEntity, ERROR_LOAN_SENDING_TO_RISK_ENGINE_SQS.name()));
      response =
          new Response(
              ValidatorUtils.getListValidations(
                  ERROR_LOAN_SENDING_TO_RISK_ENGINE_SQS.name(),
                  String.valueOf(BAD_GATEWAY.value())));
    }
    return response;
  }
}
