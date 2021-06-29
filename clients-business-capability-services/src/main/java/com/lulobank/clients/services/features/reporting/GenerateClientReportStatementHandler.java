package com.lulobank.clients.services.features.reporting;

import static com.lulobank.core.utils.ValidatorUtils.getListValidations;

import com.lulobank.clients.services.events.NewReportEvent;
import com.lulobank.clients.services.features.reporting.model.GenerateClientReportStatement;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.clients.services.utils.ClientErrorResultsEnum;
import com.lulobank.clients.services.utils.NewReportEventBuilder;
import com.lulobank.core.Handler;
import com.lulobank.core.Response;
import java.util.Optional;
import org.springframework.http.HttpStatus;

public class GenerateClientReportStatementHandler
    implements Handler<Response<NewReportEvent>, GenerateClientReportStatement> {

  private ClientsRepository clientsRepository;

  public GenerateClientReportStatementHandler(ClientsRepository clientsRepository) {
    this.clientsRepository = clientsRepository;
  }

  @Override
  public Response<NewReportEvent> handle(
      GenerateClientReportStatement generateClientReportStatement) {
    Response response;
    Optional<ClientEntity> clientEntityOptional =
        clientsRepository.findByIdClient(generateClientReportStatement.getIdClient());
    if (clientEntityOptional.isPresent()) {
      response = new Response<>(getNewReportEvent(generateClientReportStatement));
    } else {
      response =
          new Response<>(
              getListValidations(
                  ClientErrorResultsEnum.CLIENT_NOT_FOUND_IN_DB.name(),
                  String.valueOf(HttpStatus.NOT_FOUND.value())));
    }
    return response;
  }

  private NewReportEvent getNewReportEvent(
      GenerateClientReportStatement generateClientReportStatement) {
    return NewReportEventBuilder.newReportEventBuilder()
        .withIdClient(generateClientReportStatement.getIdClient())
        .withIdProduct(generateClientReportStatement.getIdProduct())
        .withTypeReport(generateClientReportStatement.getTypeReport())
        .withInitialPeriod(generateClientReportStatement.getInitialPeriod())
        .withFinalPeriod(generateClientReportStatement.getFinalPeriod())
        .build();
  }
}
