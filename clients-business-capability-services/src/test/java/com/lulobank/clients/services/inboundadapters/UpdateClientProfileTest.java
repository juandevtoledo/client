package com.lulobank.clients.services.inboundadapters;

import com.lulobank.clients.sdk.operations.dto.UpdateClientAddressRequest;
import com.lulobank.clients.services.features.profile.UpdateClientAddressUseCase;
import com.lulobank.clients.services.features.profile.UpdateClientAddressService;
import com.lulobank.clients.services.features.profile.mapper.ClientsEntityV3Mapper;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.model.OnBoardingStatus;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.Test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static com.lulobank.clients.sdk.operations.util.CheckPoints.FINISH_ON_BOARDING;


public class UpdateClientProfileTest extends AbstractBaseUnitTest {

  private static final String ID_CLIENT = "1106bc49-4a0f-4f52-86ca-1994bb3c26d9";
  private static final String ADDRESS = "address_test";
  private static final String ADDRESS_2 = "address_test2";
  private static final String ADDRESS_PREFIX = "prefix_test";
  private static final String ADDRESS_PREFIX_2 = "prefix_test2";
  private static final String ADDRESS_COMPLEMENT = "complement_test";
  private static final String ADDRESS_COMPLEMENT_2 = "complement_test2";
  private static final String DEPARTMENT = "department_test";
  private static final String DEPARTMENT_ID = "1";
  private static final String DEPARTMENT_2 = "department_test2";
  private static final String DEPARTMENT_ID_2 = "2";
  private static final String CITY = "city_test";
  private static final String CITY_ID = "1";
  private static final String CITY_2 = "city_test2";
  private static final String CITY_ID_2 = "2";
  private static final String CODE = "ADL";
  private static final int PHONE_PREFIX = 57;
  private static final String PHONE_NUMBER_1 = "3102897766";
  private static final String NEW_EMAIL = "newmail@mail.com";

  private UpdateClientAddressUseCase useCase;
  private UpdateClientAddressService updateClientAddressService;

  private ClientEntity clientEntity;
  private UpdateClientAddressRequest updateClientRequest;

  @Override
  protected void init() {


    updateClientAddressService = new UpdateClientAddressService(clientsV3Repository,messageService,clientInfoCoreBankingPort);

    useCase =new UpdateClientAddressUseCase(updateClientAddressService);

    clientEntity = new ClientEntity();
    clientEntity.setIdClient(ID_CLIENT);
    clientEntity.setAddress(ADDRESS);
    clientEntity.setAddressPrefix(ADDRESS_PREFIX);
    clientEntity.setAddressComplement(ADDRESS_COMPLEMENT);
    clientEntity.setDepartment(DEPARTMENT);
    clientEntity.setDepartmentId(DEPARTMENT_ID);
    clientEntity.setCity(CITY);
    clientEntity.setCityId(CITY_ID);
    clientEntity.setCode(CODE);
    clientEntity.setPhonePrefix(PHONE_PREFIX);
    clientEntity.setPhoneNumber(PHONE_NUMBER_1);
    clientEntity.setEmailAddress(NEW_EMAIL);
    clientEntity.setOnBoardingStatus(new OnBoardingStatus());

    updateClientRequest = new UpdateClientAddressRequest();
    updateClientRequest.setIdClient(ID_CLIENT);
    updateClientRequest.setAddress(ADDRESS_2);
    updateClientRequest.setAddressPrefix(ADDRESS_PREFIX_2);
    updateClientRequest.setAddressComplement(ADDRESS_COMPLEMENT_2);
    updateClientRequest.setDepartment(DEPARTMENT_2);
    updateClientRequest.setDepartmentId(DEPARTMENT_ID_2);
    updateClientRequest.setCity(CITY_2);
    updateClientRequest.setCityId(CITY_ID_2);
    updateClientRequest.setSendNotification(true);
    updateClientRequest.setCheckpoint(FINISH_ON_BOARDING.name());

  }

  @Test
  public void shouldReturnOkSinceAddressUpdated() {
    updateClientRequest.setIdClient(ID_CLIENT);
    updateClientRequest.setCity(CITY);
    updateClientRequest.setCityId(CITY_ID);
    updateClientRequest.setDepartment(DEPARTMENT);
    updateClientRequest.setDepartmentId(DEPARTMENT_ID);
    updateClientRequest.setCode(CODE);
    when(clientsV3Repository.findByIdClient(any(String.class))).thenReturn(Option.of(ClientsEntityV3Mapper.INSTANCE.toClientsV3Entity(clientEntity)));
    Try<Boolean> response = useCase.execute(updateClientRequest);
    assertTrue(response.isSuccess());
    verify(clientInfoCoreBankingPort, times(1)).updateAddressCoreBanking(any(),any());
    verify(clientsV3Repository, times(1)).save(clientEntityV3ArgumentCaptor.capture());
    verify(messageService, timeout(200)).sendNotificationUpdateAddress(any());
    assertEquals(ID_CLIENT, clientEntityV3ArgumentCaptor.getValue().getIdClient());
    assertEquals(ADDRESS_2, clientEntityV3ArgumentCaptor.getValue().getAddress());
  }

  @Test
  public void should_Return_Ok_Since_City_Updated() {
    updateClientRequest.setIdClient(ID_CLIENT);
    updateClientRequest.setAddress(ADDRESS);
    updateClientRequest.setAddressPrefix(ADDRESS_PREFIX);
    updateClientRequest.setAddressComplement(ADDRESS_COMPLEMENT);
    updateClientRequest.setDepartment(DEPARTMENT);
    updateClientRequest.setDepartmentId(DEPARTMENT_ID);
    updateClientRequest.setCode(CODE);
    when(clientsV3Repository.findByIdClient(any(String.class))).thenReturn(Option.of(ClientsEntityV3Mapper.INSTANCE.toClientsV3Entity(clientEntity)));
    Try<Boolean> response = useCase.execute(updateClientRequest);
    assertTrue(response.isSuccess());
    verify(clientInfoCoreBankingPort, times(1)).updateAddressCoreBanking(any(),any());
    verify(clientsV3Repository, times(1)).save(clientEntityV3ArgumentCaptor.capture());
    verify(messageService, timeout(200)).sendNotificationUpdateAddress(any());
    assertEquals(ID_CLIENT, clientEntityV3ArgumentCaptor.getValue().getIdClient());
    assertEquals(CITY_2, clientEntityV3ArgumentCaptor.getValue().getCity());
    assertEquals(CITY_ID_2, clientEntityV3ArgumentCaptor.getValue().getCityId());
  }

  @Test
  public void shouldReturnOkSinceDepartmentUpdated() {
    updateClientRequest.setIdClient(ID_CLIENT);
    updateClientRequest.setAddress(ADDRESS);
    updateClientRequest.setAddressPrefix(ADDRESS_PREFIX);
    updateClientRequest.setAddressComplement(ADDRESS_COMPLEMENT);
    updateClientRequest.setCity(CITY);
    updateClientRequest.setCityId(CITY_ID);
    updateClientRequest.setCode(CODE);
    when(clientsV3Repository.findByIdClient(any(String.class))).thenReturn(Option.of(ClientsEntityV3Mapper.INSTANCE.toClientsV3Entity(clientEntity)));
    Try<Boolean> response = useCase.execute(updateClientRequest);
    assertTrue(response.isSuccess());
    verify(clientInfoCoreBankingPort, times(1)).updateAddressCoreBanking(any(),any());
    verify(clientsV3Repository, times(1)).save(clientEntityV3ArgumentCaptor.capture());
    verify(messageService, timeout(200)).sendNotificationUpdateAddress(any());
    assertEquals(ID_CLIENT, clientEntityV3ArgumentCaptor.getValue().getIdClient());
    assertEquals(DEPARTMENT_2, clientEntityV3ArgumentCaptor.getValue().getDepartment());
    assertEquals(DEPARTMENT_ID_2, clientEntityV3ArgumentCaptor.getValue().getDepartmentId());
  }

  @Test
  public void shouldReturnOkSinceAddressDoesntChange() {
    updateClientRequest.setIdClient(ID_CLIENT);
    updateClientRequest.setAddress(ADDRESS);
    updateClientRequest.setAddressPrefix(ADDRESS_PREFIX);
    updateClientRequest.setAddressComplement(ADDRESS_COMPLEMENT);
    updateClientRequest.setDepartment(DEPARTMENT);
    updateClientRequest.setDepartmentId(DEPARTMENT_ID);
    updateClientRequest.setCity(CITY);
    updateClientRequest.setCityId(CITY_ID);
    updateClientRequest.setCode(CODE);
    when(clientsV3Repository.findByIdClient(any(String.class))).thenReturn(Option.of(ClientsEntityV3Mapper.INSTANCE.toClientsV3Entity(clientEntity)));
    Try<Boolean> response = useCase.execute(updateClientRequest);
    assertTrue(response.isSuccess());
    verify(clientInfoCoreBankingPort, times(0)).updateAddressCoreBanking(any(),any());
    verify(clientsV3Repository, timeout(200).times(0)).save(any());
    verify(messageService, times(0)).sendNotificationUpdateAddress(any());
  }

  @Test
  public void shouldReturnNotFoundSinceIdClientNotFound() {
    when(clientsV3Repository.findByIdClient(any(String.class))).thenReturn(Option.none());
    Try<Boolean> response = useCase.execute(updateClientRequest);
    assertTrue(response.isFailure());
    verify(clientInfoCoreBankingPort, times(0)).updateAddressCoreBanking(any(),any());
    verify(clientsRepository, times(0)).save(any(ClientEntity.class));
    verify(clientsV3Repository, timeout(200).times(0)).save(any());

  }
}
