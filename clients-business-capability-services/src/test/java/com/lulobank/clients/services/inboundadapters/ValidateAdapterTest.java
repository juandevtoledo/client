package com.lulobank.clients.services.inboundadapters;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ValidateAdapterTest {

  private ValidationsAdapter testedClass;

  @Mock private ClientsRepository clientsRepository;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    testedClass = new ValidationsAdapter(clientsRepository);
  }

  @Test
  public void Should_Return_PRECONDITION_FAILED_Since_PhoneNumber_Does_Exist() {
    when(clientsRepository.findByPhonePrefixAndPhoneNumber(anyInt(), any(String.class)))
        .thenReturn(Optional.of(new ClientEntity()));
    ResponseEntity<String> response =
        testedClass.validatePhone(new HttpHeaders(), 57, "3164375977");
    assertEquals(HttpStatus.PRECONDITION_FAILED, response.getStatusCode());
  }

  @Test
  public void Should_Return_OK_Since_PhoneNumber_Does_Not_Exist() {
    when(clientsRepository.findByPhonePrefixAndPhoneNumber(anyInt(), any(String.class)))
        .thenReturn(Optional.empty());
    ResponseEntity<String> response =
        testedClass.validatePhone(new HttpHeaders(), 57, "3164375978");
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  public void Should_Return_PRECONDITION_FAILED_Since_Client_is_create_completed() {
    ClientEntity clientEntity = new ClientEntity();
    clientEntity.setIdCard("256282929");
    clientEntity.setIdClient(UUID.randomUUID().toString());
    clientEntity.setEmailAddress("test@lulobank.com");
    clientEntity.setPhoneNumber("3135057854");
    clientEntity.setDateOfIssue(LocalDate.now());
    clientEntity.setIdCognito("92929");
    when(clientsRepository.findByIdCard(anyString())).thenReturn(clientEntity);
    ResponseEntity<String> response = testedClass.validateIdCard(new HttpHeaders(), "256282929");
    assertEquals(HttpStatus.PRECONDITION_FAILED, response.getStatusCode());
  }

  @Test
  public void Should_Return_OK_Since_Client_is_not_create_completed() {
    ClientEntity clientEntity = new ClientEntity();
    clientEntity.setIdCard("256282929");
    clientEntity.setIdClient(UUID.randomUUID().toString());
    clientEntity.setDateOfIssue(LocalDate.now());
    when(clientsRepository.findByIdCard(anyString())).thenReturn(clientEntity);
    ResponseEntity<String> response = testedClass.validateIdCard(new HttpHeaders(), "256282929");
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  public void Should_Return_OK_Since_IdCard_Does_Not_Exist() {
    when(clientsRepository.findByIdCard(anyString())).thenReturn(null);
    ResponseEntity<String> response = testedClass.validateIdCard(new HttpHeaders(), "3164375978");
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }
}
