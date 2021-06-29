package com.lulobank.clients.starter;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lulobank.clients.sdk.operations.util.CheckPoints;
import com.lulobank.clients.services.events.CreditAccepted;
import com.lulobank.clients.services.features.onboardingclients.UpdateCreditAcceptHandler;
import com.lulobank.clients.services.inboundadapters.SqsClientListenerAdapter;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.model.OnBoardingStatus;
import com.lulobank.clients.services.utils.ProductTypeEnum;
import com.lulobank.core.events.Event;
import com.lulobank.core.utils.EventUtils;
import java.util.HashMap;
import org.junit.Test;

public class SqsUpdateCheckPointTest extends AbstractBaseIntegrationTest {

  private static final String ID_CLIENT = "aa9504b5-0485-4fc8-aacc-8d9f64092664";
  private static final String ID_APPLICANT = "id-applicant-01";
  private Event<CreditAccepted> creditAcceptedEvent;
  private UpdateCreditAcceptHandler updateCreditAcceptHandler;

  private ClientEntity clientEntity;
  private SqsClientListenerAdapter testedClass;

  @Override
  protected void init() {
    updateCreditAcceptHandler = new UpdateCreditAcceptHandler(clientsRepository);
    testedClass =
        new SqsClientListenerAdapter(clientsRepository, null, updateCreditAcceptHandler, null,null);

    clientEntity = new ClientEntity();
    clientEntity.setIdClient(ID_CLIENT);
    OnBoardingStatus onBoardingStatus = new OnBoardingStatus();
    onBoardingStatus.setProductSelected(ProductTypeEnum.CREDIT_ACCOUNT.name());
    onBoardingStatus.setCheckpoint(CheckPoints.ON_BOARDING.name());
    clientEntity.setOnBoardingStatus(onBoardingStatus);
  }

  @Test
  public void shouldUpdateCreditAcceptStatus() throws JsonProcessingException {
    setCreditAcceptEvent(CheckPoints.FINISH_ON_BOARDING);
    String eventRequest = objectMapper.writeValueAsString(creditAcceptedEvent);
    when(clientsRepository.findByIdClientAndOnBoardingStatusNotNull(any()))
        .thenReturn(java.util.Optional.ofNullable(clientEntity));
    testedClass.getMessage(new HashMap<>(), eventRequest);
    verify(clientsRepository, times(1)).save(clientEntityArgumentCaptor.capture());
    assertEquals(
        CheckPoints.FINISH_ON_BOARDING.name(),
        clientEntityArgumentCaptor.getValue().getOnBoardingStatus().getCheckpoint());
  }

  @Test
  public void notUpdateCreditAcceptStatusSinceClientNotFound() throws JsonProcessingException {
    setCreditAcceptEvent(CheckPoints.FINISH_ON_BOARDING);
    String eventRequest = objectMapper.writeValueAsString(creditAcceptedEvent);
    when(clientsRepository.findByIdClientAndOnBoardingStatusNotNull(any()))
        .thenReturn(java.util.Optional.empty());
    testedClass.getMessage(new HashMap<>(), eventRequest);
    verify(clientsRepository, times(0)).save(new ClientEntity());
  }

  @Test
  public void notUpdateCreditAcceptSinceStatusIsInvalid() throws JsonProcessingException {
    setCreditAcceptEvent(CheckPoints.FINISH_ON_BOARDING);
    creditAcceptedEvent.getPayload().setCheckpoint("EVENT_FAILED");
    String eventRequest = objectMapper.writeValueAsString(creditAcceptedEvent);
    when(clientsRepository.findByIdClientAndOnBoardingStatusNotNull(any()))
        .thenReturn(java.util.Optional.ofNullable(clientEntity));
    testedClass.getMessage(new HashMap<>(), eventRequest);
    verify(clientsRepository, times(0)).save(new ClientEntity());
  }

  @Test
  public void notUpdateCreditAcceptSincePayLoadIsnull() throws JsonProcessingException {
    setCreditAcceptEvent(CheckPoints.FINISH_ON_BOARDING);
    creditAcceptedEvent.setPayload(null);
    String eventRequest = objectMapper.writeValueAsString(creditAcceptedEvent);
    when(clientsRepository.findByIdClientAndOnBoardingStatusNotNull(any()))
        .thenReturn(java.util.Optional.ofNullable(clientEntity));
    testedClass.getMessage(new HashMap<>(), eventRequest);
    verify(clientsRepository, times(0)).save(new ClientEntity());
  }

  @Test
  public void notUpdateCreditAcceptSinceNewCheckPointsIsInvalid() throws JsonProcessingException {
    setCreditAcceptEvent(CheckPoints.ON_BOARDING);
    clientEntity.getOnBoardingStatus().setCheckpoint(CheckPoints.FINISH_ON_BOARDING.name());
    String eventRequest = objectMapper.writeValueAsString(creditAcceptedEvent);
    when(clientsRepository.findByIdClientAndOnBoardingStatusNotNull(any()))
        .thenReturn(java.util.Optional.ofNullable(clientEntity));
    testedClass.getMessage(new HashMap<>(), eventRequest);
    verify(clientsRepository, times(0)).save(new ClientEntity());
  }

  @Test
  public void notUpdateCreditAcceptSinceOnboardingIsNotPresent() throws JsonProcessingException {
    setCreditAcceptEvent(CheckPoints.FINISH_ON_BOARDING);
    clientEntity.setOnBoardingStatus(null);
    String eventRequest = objectMapper.writeValueAsString(creditAcceptedEvent);
    when(clientsRepository.findByIdClientAndOnBoardingStatusNotNull(any()))
        .thenReturn(java.util.Optional.empty());
    testedClass.getMessage(new HashMap<>(), eventRequest);
    verify(clientsRepository, times(0)).save(new ClientEntity());
  }

  private void setCreditAcceptEvent(CheckPoints checkPoints) {
    CreditAccepted checkPointClient = new CreditAccepted();
    checkPointClient.setIdClient(ID_CLIENT);
    checkPointClient.setCheckpoint(checkPoints.name());
    creditAcceptedEvent = new EventUtils().getEvent(checkPointClient);
  }
}
