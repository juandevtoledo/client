package com.lulobank.clients.services.inboundadapters;

import com.lulobank.clients.sdk.operations.dto.ClientErrorResult;
import com.lulobank.clients.sdk.operations.dto.ClientResult;
import com.lulobank.clients.services.events.NewReportEvent;
import com.lulobank.clients.services.features.reporting.GenerateClientReportStatementHandler;
import com.lulobank.clients.services.features.reporting.action.SendMessageToSQSReporting;
import com.lulobank.clients.services.features.reporting.model.GenerateClientReportStatement;
import com.lulobank.clients.services.features.reporting.validators.GenerateClientReportValidator;
import com.lulobank.clients.services.outboundadapters.SendMessageToSQS;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.core.Response;
import com.lulobank.core.actions.Action;
import com.lulobank.core.crosscuttingconcerns.PostActionsDecoratorHandler;
import com.lulobank.core.crosscuttingconcerns.ValidatorDecoratorHandler;
import com.lulobank.core.utils.ValidatorUtils;
import com.lulobank.core.validations.Validator;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*")
public class ReportAdapter {

  private ClientsRepository repository;
  private QueueMessagingTemplate queueMessagingTemplate;

  @Value("${cloud.aws.sqs.reporting-events}")
  private String sqsReporting;

  public ReportAdapter(
      QueueMessagingTemplate queueMessagingTemplate, ClientsRepository repository) {
    this.repository = repository;
    this.queueMessagingTemplate = queueMessagingTemplate;
  }

  @PostMapping(value = "/reports")
  public ResponseEntity<ClientResult> generateReport(
      @RequestHeader final HttpHeaders headers,
      @RequestBody final GenerateClientReportStatement generateClientReportStatement) {

    generateClientReportStatement.setHttpHeaders(headers.toSingleValueMap());
    List<Validator<GenerateClientReportStatement>> validators = new ArrayList<>();
    validators.add(new GenerateClientReportValidator());

    List<Action<Response<NewReportEvent>, GenerateClientReportStatement>> actions =
        new ArrayList<>();

    SendMessageToSQS messageToSQS =
        new SendMessageToSQSReporting(queueMessagingTemplate, sqsReporting);
    actions.add(messageToSQS);

    Response<NewReportEvent> response =
        new ValidatorDecoratorHandler<>(
                new PostActionsDecoratorHandler<>(
                    new GenerateClientReportStatementHandler(repository), actions),
                validators)
            .handle(generateClientReportStatement);

    if (Boolean.FALSE.equals(response.getHasErrors())) {
      return ResponseEntity.accepted().build();
    }

    return new ResponseEntity<>(
        new ClientErrorResult(response.getErrors()),
        ValidatorUtils.getHttpStatusByCode(response.getErrors().get(0).getValue()));
  }
}
