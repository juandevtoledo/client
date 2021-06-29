package com.lulobank.clients.services.inboundadapters;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.lulobank.clients.services.domain.StateBlackList;
import com.lulobank.clients.services.features.riskengine.model.ClientCreated;
import com.lulobank.clients.services.features.riskengine.model.ClientWithIdCardInformation;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.core.Response;
import java.time.LocalDate;
import java.util.Objects;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ClientRiskEngineTest {
  @InjectMocks private ClientAdapter testedClass;
  private ClientWithIdCardInformation clientWithIdCardInformation;
  private ClientEntity clientEntity;
  private String UUDI = "61df1e72-6eca-4245-a9c8-7df7998d9630";
  @Mock private ClientsRepository clientsRepository;
  @Mock private QueueMessagingTemplate queueMessagingTemplate;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    clientWithIdCardInformation = new ClientWithIdCardInformation();
    clientWithIdCardInformation.setIdCard("1067843466");
    clientWithIdCardInformation.setDateOfIssue("2019-01-01");
  }

  @Test
  public void should_Return_ACCEPTED_Since_Client_Should_Saved() {
    clientEntity = new ClientEntity();
    clientEntity.setIdClient(UUDI);
    //
    when(clientsRepository.findByIdCard(any(String.class))).thenReturn(null);
    when(clientsRepository.save(any(ClientEntity.class))).thenReturn(clientEntity);
    ResponseEntity<Response<ClientCreated>> response =
        testedClass.initialSaveClient(new HttpHeaders(), clientWithIdCardInformation);
    assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    assertEquals(this.UUDI, clientEntity.getIdClient());
    //
  }

  @Test
  public void should_Return_FOUND_Since_Client_is_create() {
    clientEntity = new ClientEntity();
    clientEntity.setIdClient(UUDI);
    clientEntity.setIdCard(UUDI);
    //
    ClientEntity clientFound = new ClientEntity();
    clientFound.setIdCard("256282929");
    clientFound.setIdClient(UUDI);
    clientFound.setEmailAddress("test@lulobank.com");
    clientFound.setPhoneNumber("3135057854");
    clientFound.setDateOfIssue(LocalDate.now());
    clientFound.setIdCognito("oxoxo");
    when(clientsRepository.findByIdCard(any(String.class))).thenReturn(clientFound);
    when(clientsRepository.save(any(ClientEntity.class))).thenReturn(clientEntity);
    ResponseEntity<Response<ClientCreated>> response =
        testedClass.initialSaveClient(new HttpHeaders(), clientWithIdCardInformation);
    //
    assertEquals(HttpStatus.FOUND, response.getStatusCode());
  }

  @Test
  public void should_Return_ACCEPTED_Since_Client_exist_and_dont_save_entity() {
    clientEntity = null;
    //
    ClientEntity clientFound = new ClientEntity();
    clientFound.setIdClient(UUDI);
    clientFound.setIdCard("1067843466");
    //
    when(clientsRepository.findByIdCard(any(String.class))).thenReturn(clientFound);
    when(clientsRepository.save(any(ClientEntity.class))).thenReturn(clientEntity);
    ResponseEntity<Response<ClientCreated>> response =
        testedClass.initialSaveClient(new HttpHeaders(), clientWithIdCardInformation);
    //
    assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    assertEquals(Boolean.TRUE, Objects.isNull(clientEntity));
  }

  @Test
  public void should_Return_ACCEPTED_Since_Client_exist_without_send_SQS() {
    clientEntity = null;
    ClientEntity clientFound = new ClientEntity();
    clientFound.setIdClient(UUDI);
    clientFound.setIdCard("1067843466");
    when(clientsRepository.findByIdCard(any(String.class))).thenReturn(clientFound);
    when(clientsRepository.save(any(ClientEntity.class))).thenReturn(clientEntity);
    ResponseEntity<Response<ClientCreated>> response =
        testedClass.validateClient(new HttpHeaders(), clientWithIdCardInformation);
    assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    assertEquals(Boolean.TRUE, Objects.isNull(clientEntity));
  }

  @Test
  public void should_Return_FOUND_Since_Client_is_confirmed() {
    ClientEntity clientEntityConfirmed = new ClientEntity();
    clientEntityConfirmed.setIdClient(UUDI);
    clientEntityConfirmed.setIdCard("1067843466");
    clientEntityConfirmed.setName("Pepito");
    clientEntityConfirmed.setLastName("Perez");
    clientEntityConfirmed.setAddress("Calle falsa 123");
    clientEntityConfirmed.setPhonePrefix(57);
    clientEntityConfirmed.setPhoneNumber("30000000");
    clientEntityConfirmed.setEmailAddress("preuba@prueba");
    clientEntityConfirmed.setBlackListState(StateBlackList.NON_BLACKLISTED.name());
    clientEntityConfirmed.setIdCognito("2323213124341");
    when(clientsRepository.findByIdCard(any(String.class))).thenReturn(clientEntityConfirmed);
    ResponseEntity<Response<ClientCreated>> response =
        testedClass.validateClient(new HttpHeaders(), clientWithIdCardInformation);
    assertEquals(HttpStatus.FOUND, response.getStatusCode());
  }
}
