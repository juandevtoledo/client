package com.lulobank.clients.services.inboundadapters;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lulobank.clients.sdk.operations.dto.ClientResult;
import com.lulobank.clients.services.features.reporting.model.GenerateClientReportStatement;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.core.helpers.GlobalConstants;
import com.lulobank.reporting.sdk.operations.dto.TypeReport;
import java.util.Map;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ReportAdapterTest {

  private ReportAdapter reportAdapter;

  @Mock private QueueMessagingTemplate queueMessagingTemplate;
  @Mock private ClientsRepository clientsRepository;
  @Captor private ArgumentCaptor<Map<String, Object>> mapArgumentCaptor;

  private GenerateClientReportStatement generateClientReportStatement;

  private static final String ID_CLIENT = "1106bc49-4a0f-4f52-86ca-1994bb3c26d9";
  private static final String ID_PRODUCT = "124324";
  private static final TypeReport PRODUCT_TYPE = TypeReport.ACCOUNTCERTIFICATE;
  private static final String FROM_DATE_REPORT = "2018-08";
  private static final String TO_DATE_REPORT = "2019-01";
  private static final String HEADER = "authorization";
  private static final String HEADER_CONTENT = "asd123";

  private ClientEntity clientEntity;
  private HttpHeaders headers;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    reportAdapter = new ReportAdapter(queueMessagingTemplate, clientsRepository);

    generateClientReportStatement = new GenerateClientReportStatement();
    generateClientReportStatement.setIdClient(ID_CLIENT);
    generateClientReportStatement.setIdProduct(ID_PRODUCT);
    generateClientReportStatement.setTypeReport(PRODUCT_TYPE);
    generateClientReportStatement.setInitialPeriod(FROM_DATE_REPORT);
    generateClientReportStatement.setFinalPeriod(TO_DATE_REPORT);

    clientEntity = new ClientEntity();
    clientEntity.setIdClient(ID_CLIENT);

    headers = new HttpHeaders();
    headers.add(HEADER, HEADER_CONTENT);
  }

  @Test
  public void Should_Return_NOT_FOUND_Since_Report_User_Not_Found() {

    when(clientsRepository.findByIdClient(any(String.class))).thenReturn(Optional.empty());
    ResponseEntity<ClientResult> response =
        reportAdapter.generateReport(new HttpHeaders(), generateClientReportStatement);
    verify(queueMessagingTemplate, times(0)).convertAndSend((String) isNull(), any(Object.class));
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void Should_Return_OK_Since_Message_Published_On_SQS() {
    when(clientsRepository.findByIdClient(any(String.class)))
        .thenReturn(Optional.ofNullable(clientEntity));
    ResponseEntity<ClientResult> response =
        reportAdapter.generateReport(headers, generateClientReportStatement);
    verify(queueMessagingTemplate, times(1))
        .convertAndSend((String) isNull(), any(Object.class), mapArgumentCaptor.capture());
    assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    assertEquals(headers.get(HEADER).get(0), mapArgumentCaptor.getValue().get(HEADER));
  }

  @Test
  public void Should_Return_PRECONDITION_FAILED_Since_Date_Wring_Format() {

    generateClientReportStatement.setInitialPeriod("08-2019");
    generateClientReportStatement.setFinalPeriod("08-2019");
    when(clientsRepository.findByIdClient(any(String.class))).thenReturn(Optional.empty());
    ResponseEntity<ClientResult> response =
        reportAdapter.generateReport(new HttpHeaders(), generateClientReportStatement);
    verify(queueMessagingTemplate, times(0)).convertAndSend((String) isNull(), any(Object.class));
    assertEquals(HttpStatus.PRECONDITION_FAILED, response.getStatusCode());
  }

  @Test
  public void Should_Return_PRECONDITION_FAILED_Since_Empty_Values() {

    generateClientReportStatement.setIdClient(GlobalConstants.EMPTY_STRING);
    generateClientReportStatement.setIdProduct(GlobalConstants.EMPTY_STRING);
    when(clientsRepository.findByIdClient(any(String.class))).thenReturn(Optional.empty());
    ResponseEntity<ClientResult> response =
        reportAdapter.generateReport(new HttpHeaders(), generateClientReportStatement);
    verify(queueMessagingTemplate, times(0)).convertAndSend((String) isNull(), any(Object.class));
    assertEquals(HttpStatus.PRECONDITION_FAILED, response.getStatusCode());
  }
}
