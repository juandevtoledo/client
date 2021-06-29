package com.lulobank.clients.starter.v3.adapters.out.dynamo;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.model.UpdateItemResult;
import com.lulobank.clients.services.domain.StateBlackList;
import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.starter.adapter.Constant;
import com.lulobank.clients.starter.v3.adapters.out.Sample;
import com.lulobank.clients.starter.v3.adapters.out.dynamo.dto.ClientEntity;
import com.lulobank.clients.starter.v3.mapper.ClientsEntityV3Mapper;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;

import com.lulobank.tracing.DatabaseBrave;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.*;

public class ClientsAdapterV3RepositoryTest {

    private static final String ID_CLIENT = "ID_CLIENT_TEST";

    @Mock
    private DynamoDBMapper dynamoDBMapper;

    @Mock
    private DynamoDB dynamoDB;

    @Mock
    private DatabaseBrave databaseBrave;

    @Mock
    private Table table;
    
    @Mock
    private PaginatedScanList<ClientEntity> scanList;
    
    @Mock
    private PaginatedQueryList<ClientEntity> queryList;

    private ClientsAdapterV3Repository clientsAdapterV3Repository;

    private ClientsV3Entity clientsV3Entity;

    @Captor
    private ArgumentCaptor<UpdateItemSpec> updateItemSpecArgumentCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        clientsAdapterV3Repository = new ClientsAdapterV3Repository(dynamoDBMapper, dynamoDB, databaseBrave);


        clientsV3Entity = new ClientsV3Entity();
        clientsV3Entity.setIdClient(ID_CLIENT);
        clientsV3Entity.setDateOfIssue(LocalDate.now());
        clientsV3Entity.setIdCard("12345678");
        clientsV3Entity.setBlackListState(StateBlackList.WHITELISTED.name());
        clientsV3Entity.setBlackListDate(LocalDateTime.parse(Sample.DATE));
        clientsV3Entity.setWhitelistExpirationDate(LocalDateTime.parse(Sample.DATE));

        when(dynamoDBMapper.scan(eq(ClientEntity.class), any())).thenReturn(scanList);

    }

    @Test
    public void shouldFindByIdClientOk() {
        when(dynamoDBMapper.load(ClientEntity.class,ID_CLIENT)).thenReturn(new ClientEntity());
        when(databaseBrave.query(any())).thenReturn(new ClientEntity());
        Option<ClientsV3Entity> client = clientsAdapterV3Repository.findByIdClient(ID_CLIENT);

        assertThat(client.isDefined(), is(true));
        assertThat(client.get(), notNullValue());
        assertThat(client.get(), isA(ClientsV3Entity.class));

        verify(databaseBrave).query(any());
    }

    @Test
    public void shouldNotFindByIdClient() {
        when(dynamoDBMapper.load(ClientEntity.class,ID_CLIENT)).thenReturn(null);
        when(databaseBrave.query(any())).thenReturn(null);
        Option<ClientsV3Entity> client = clientsAdapterV3Repository.findByIdClient(ID_CLIENT);

        assertThat(client.isDefined(), is(false));

        verify(databaseBrave).query(any());
    }


    @Test
    public void shouldUpdateLoanRequestedOk() {
        clientsAdapterV3Repository.save(clientsV3Entity);
        verify(databaseBrave).save(any());
    }

    @Test
    public void shouldNotUpdateLoanRequestedWhenDynamoFailed() throws UnsupportedOperationException {
        doThrow(UnsupportedOperationException.class).when(dynamoDBMapper).save(Mockito.any());

        Try<ClientsV3Entity> response =  clientsAdapterV3Repository.save(clientsV3Entity);

        assertThat(response.isFailure(), is(true));
    }

    @Test
    public void shouldUpdateClientBlacklisted() {
        when(dynamoDB.getTable(any())).thenReturn(table);
        when(table.updateItem(updateItemSpecArgumentCaptor.capture()))
                .thenReturn(new UpdateItemOutcome(new UpdateItemResult()));

        Try<Void> response =  clientsAdapterV3Repository.updateClientBlacklisted(clientsV3Entity);

        assertThat(response.isSuccess(), is(true));
        assertEquals(ID_CLIENT, updateItemSpecArgumentCaptor.getValue().getKeyComponents().stream().findFirst().get().getValue());
        assertEquals(StateBlackList.WHITELISTED.name(), updateItemSpecArgumentCaptor.getValue().getValueMap().get(":blackListState"));
        assertEquals(Sample.DATE, updateItemSpecArgumentCaptor.getValue().getValueMap().get(":blackListDate"));
        assertEquals(Sample.DATE, updateItemSpecArgumentCaptor.getValue().getValueMap().get(":whitelistExpirationDate"));
    }

    @Test
    public void shouldNotUpdatePhoneNumberWhenDynamoFailed() throws UnsupportedOperationException {
        doThrow(UnsupportedOperationException.class).when(dynamoDBMapper).save(Mockito.any());

        Either<UseCaseResponseError, Boolean> response =  clientsAdapterV3Repository.updatePhoneNumber(ID_CLIENT,Sample.PHONE_NUMBER,Sample.PHONE_PREFIX);

        assertThat(response.isLeft(), is(true));
    }

    @Test
    public void shouldUpdatePhoneNumber() {
        when(dynamoDB.getTable(any())).thenReturn(table);
        when(table.updateItem(updateItemSpecArgumentCaptor.capture()))
                .thenReturn(new UpdateItemOutcome(new UpdateItemResult()));

        Either<UseCaseResponseError, Boolean> response =  clientsAdapterV3Repository.updatePhoneNumber(ID_CLIENT,Sample.PHONE_NUMBER,Sample.PHONE_PREFIX);

        assertThat(response.isRight(), is(true));
        assertEquals(ID_CLIENT, updateItemSpecArgumentCaptor.getValue().getKeyComponents().stream().findFirst().get().getValue());
        assertEquals(Sample.PHONE_NUMBER, updateItemSpecArgumentCaptor.getValue().getValueMap().get(":phoneNumber"));
        assertEquals(BigDecimal.valueOf(Sample.PHONE_PREFIX), updateItemSpecArgumentCaptor.getValue().getValueMap().get(":phonePrefix"));
    }

    @Test
    public void shouldUpdateEmailOk(){
        when(dynamoDB.getTable(any())).thenReturn(table);
        when(table.updateItem(updateItemSpecArgumentCaptor.capture()))
                .thenReturn(new UpdateItemOutcome(new UpdateItemResult()));

        Try<Void> response = clientsAdapterV3Repository.updateEmailByIdClient(ID_CLIENT, Sample.MAIL);
        assertThat(response.isFailure(), is(false));
        assertEquals(ID_CLIENT, updateItemSpecArgumentCaptor.getValue().getKeyComponents().stream().findFirst().get().getValue());
        assertEquals(Sample.MAIL, updateItemSpecArgumentCaptor.getValue().getValueMap().get(":emailAddress"));
   }

    @Test
    public void shouldNotUpdateEmailAddressWhenDynamoFailed() throws UnsupportedOperationException {
        doThrow(UnsupportedOperationException.class).when(dynamoDBMapper).save(Mockito.any());
        Try<Void> response =  clientsAdapterV3Repository.updateEmailByIdClient(ID_CLIENT,Sample.MAIL);
        assertThat(response.isFailure(), is(true));
    }


    @Test
    public void shouldNotUpdateClientBlacklisted() {
        when(dynamoDB.getTable(any())).thenThrow(new UnsupportedOperationException());
        Try<Void> response =  clientsAdapterV3Repository.updateClientBlacklisted(clientsV3Entity);
        assertThat(response.isFailure(), is(true));
    }

    @Test
    public void shouldNotFindByEmail() {
        when(scanList.stream()).thenReturn(Stream.empty());
        when(databaseBrave.query(any())).thenReturn(Optional.empty());
        Option<ClientsV3Entity> response = clientsAdapterV3Repository.findByEmailAddress(Sample.MAIL);

        assertFalse(response.isDefined());
    }

    @Test
    public void shouldNotFindByPhone() {
        when(scanList.stream()).thenReturn(Stream.empty());
        when(databaseBrave.query(any())).thenReturn(Optional.empty());
        Option<ClientsV3Entity> response = clientsAdapterV3Repository.findByPhonePrefixAndPhoneNumber(Sample.PHONE_PREFIX,Sample.PHONE_NUMBER);

        assertFalse(response.isDefined());
    }

    @Test
    public void shouldFindByPhone() {
        ClientEntity clientEntity= com.lulobank.clients.starter.utils.Sample.getClientsEntity();
        when(scanList.stream()).thenReturn(Stream.of(clientEntity));
        when(databaseBrave.query(any())).thenReturn(Optional.of(clientEntity));
        Option<ClientsV3Entity> response =  clientsAdapterV3Repository.findByPhonePrefixAndPhoneNumber(Sample.PHONE_PREFIX,Sample.PHONE_NUMBER);
        assertTrue(response.isDefined());
        ClientsV3Entity responseEntity = response.get();

        assertEquals(clientEntity.getIdClient(),responseEntity.getIdClient());
        assertEquals(clientEntity.getIdCard(),responseEntity.getIdCard());
        assertEquals(clientEntity.getAddress(),responseEntity.getAddress());
        assertEquals(clientEntity.getName(),responseEntity.getName());
        assertEquals(clientEntity.getPhoneNumber(),responseEntity.getPhoneNumber());
        assertEquals(clientEntity.getEmailAddress(),responseEntity.getEmailAddress());
        assertEquals(clientEntity.getLastName(),responseEntity.getLastName());

    }

    @Test
    public void shouldFindByEmail() {
        ClientEntity clientEntity= com.lulobank.clients.starter.utils.Sample.getClientsEntity();
        when(scanList.stream()).thenReturn(Stream.of(clientEntity));
        when(databaseBrave.query(any())).thenReturn(Optional.of(clientEntity));
        Option<ClientsV3Entity> response = clientsAdapterV3Repository.findByEmailAddress(Sample.MAIL);
        assertTrue(response.isDefined());
        ClientsV3Entity responseEntity = response.get();

        assertEquals(clientEntity.getIdClient(),responseEntity.getIdClient());
        assertEquals(clientEntity.getIdCard(),responseEntity.getIdCard());
        assertEquals(clientEntity.getAddress(),responseEntity.getAddress());
        assertEquals(clientEntity.getName(),responseEntity.getName());
        assertEquals(clientEntity.getPhoneNumber(),responseEntity.getPhoneNumber());
        assertEquals(clientEntity.getEmailAddress(),responseEntity.getEmailAddress());
        assertEquals(clientEntity.getLastName(),responseEntity.getLastName());

    }

    @Test
    public void shouldNotFindByIdCard() {
        when(scanList.stream()).thenReturn(Stream.empty());
        when(databaseBrave.query(any())).thenReturn(Optional.empty());
        Option<ClientsV3Entity> response = clientsAdapterV3Repository.findByIdCard(Constant.ID_CARD);

        assertFalse(response.isDefined());
    }

    @Test
    public void shouldFindByIdCard() {
        ClientEntity clientEntity= com.lulobank.clients.starter.utils.Sample.getClientsEntity();
        when(scanList.stream()).thenReturn(Stream.of(clientEntity));
        when(databaseBrave.query(any())).thenReturn(Optional.of(clientEntity));
        Option<ClientsV3Entity> response = clientsAdapterV3Repository.findByIdCard(Constant.ID_CARD);
        assertTrue(response.isDefined());
        ClientsV3Entity responseEntity = response.get();

        assertEquals(clientEntity.getIdClient(),responseEntity.getIdClient());
        assertEquals(clientEntity.getIdCard(),responseEntity.getIdCard());
        assertEquals(clientEntity.getAddress(),responseEntity.getAddress());
        assertEquals(clientEntity.getName(),responseEntity.getName());
        assertEquals(clientEntity.getPhoneNumber(),responseEntity.getPhoneNumber());
        assertEquals(clientEntity.getEmailAddress(),responseEntity.getEmailAddress());
        assertEquals(clientEntity.getLastName(),responseEntity.getLastName());

    }
    
    @Test
    public void shouldFindClientByIdCbs() {
    	
        String idCbs = "idCbs";
        ClientEntity clientEntity = buildClientEntity();
        when(dynamoDBMapper.query(eq(ClientEntity.class), any())).thenReturn(queryList);
        when(queryList.stream()).thenReturn(Stream.of(clientEntity));
        when(databaseBrave.query(any())).thenReturn(Optional.of(clientEntity));
        Option<ClientsV3Entity> response =  clientsAdapterV3Repository.findByIdCbs(idCbs);
        assertThat(response.isEmpty(), is(false));
    }

	private ClientEntity buildClientEntity() {
		ClientEntity clientEntity = new ClientEntity();
		clientEntity.setIdCbs("idCbs");
		clientEntity.setIdClient("idClient");
		return clientEntity;
	}
}