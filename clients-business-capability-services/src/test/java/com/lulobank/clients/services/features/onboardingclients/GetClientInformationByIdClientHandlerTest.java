package com.lulobank.clients.services.features.onboardingclients;

import static org.apache.logging.log4j.util.Strings.EMPTY;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.lulobank.clients.sdk.operations.dto.onboardingclients.ClientInformationByIdClient;
import com.lulobank.clients.sdk.operations.dto.onboardingclients.GetClientInformationByIdClient;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.core.Response;

import flexibility.client.connector.ProviderException;
import flexibility.client.sdk.FlexibilitySdk;

public class GetClientInformationByIdClientHandlerTest {

	private GetClientInformationByIdClientHandler subject;
	private static final String ADDRESS_COMPLEMENT = "The address complement";
	@Mock
	private ClientsRepository repository;
	@Mock
	private FlexibilitySdk flexibilitySdk;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		subject = new GetClientInformationByIdClientHandler(repository, flexibilitySdk);
	}

	@Test
	public void shouldReturnClientInformationWithOutSavingInfoAndUuidASidCbs() throws ProviderException {
		GetClientInformationByIdClient getClientInformationByIdClient = buildGetClientInformationByIdClient();
		String idCbs = "39fc7222-290d-497f-83ae-821b2fb2235b";
		ClientEntity clientEntity = buildClientEntity(idCbs);
		when(repository.findByIdClient(getClientInformationByIdClient.getIdClient())).thenReturn(Optional.of(clientEntity));
		when(flexibilitySdk.getAccountsByClientId(any())).thenReturn(null);
		Response<ClientInformationByIdClient> response = subject.handle(getClientInformationByIdClient);
		assertThat(response.getContent(), notNullValue());
		assertThat(response.getContent().getIdClient(), is(getClientInformationByIdClient.getIdClient()));
		assertThat(response.getContent().getIdCbs(), is(idCbs));
		assertEquals(EMPTY,response.getContent().getAddressComplement());
		verify(flexibilitySdk, times(0)).getAccountsByClientId(any());
	}
	
	@Test
	public void shouldReturnClientInformationWithSavingInfo() throws ProviderException {
		GetClientInformationByIdClient getClientInformationByIdClient = buildGetClientInformationByIdClient();
		String idCbs = "1234567890";
		ClientEntity clientEntity = buildClientEntity(idCbs);
		clientEntity.setAddressComplement(ADDRESS_COMPLEMENT);
		when(repository.findByIdClient(getClientInformationByIdClient.getIdClient())).thenReturn(Optional.of(clientEntity));
		when(flexibilitySdk.getAccountsByClientId(any())).thenReturn(null);
		Response<ClientInformationByIdClient> response = subject.handle(getClientInformationByIdClient);
		assertThat(response.getContent(), notNullValue());
		assertThat(response.getContent().getIdClient(), is(getClientInformationByIdClient.getIdClient()));
		assertThat(response.getContent().getIdCbs(), is(idCbs));
		assertEquals(ADDRESS_COMPLEMENT,response.getContent().getAddressComplement());
		verify(flexibilitySdk).getAccountsByClientId(any());
	}
	
	@Test
	public void shouldReturnClientInformationWithOutSavingInfo() {
		GetClientInformationByIdClient getClientInformationByIdClient = buildGetClientInformationByIdClient();
		ClientEntity clientEntity = buildClientEntity(null);
		when(repository.findByIdClient(getClientInformationByIdClient.getIdClient())).thenReturn(Optional.of(clientEntity));
		Response<ClientInformationByIdClient> response = subject.handle(getClientInformationByIdClient);
		assertThat(response.getContent(), notNullValue());
		assertThat(response.getContent().getIdClient(), is(getClientInformationByIdClient.getIdClient()));
		assertThat(response.getContent().getIdCbs(), nullValue());
	}

	private ClientEntity buildClientEntity(String idCbs) {
		ClientEntity clientEntity = new ClientEntity();
		clientEntity.setIdClient("idClient");
		clientEntity.setIdCbs(idCbs);
		return clientEntity;
	}

	private GetClientInformationByIdClient buildGetClientInformationByIdClient() {
		return  new GetClientInformationByIdClient("idClient");
	}
}
