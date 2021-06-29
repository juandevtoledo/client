package com.lulobank.clients.v3.usecase.mapper;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import com.lulobank.clients.services.utils.IdentityBiometricStatus;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.IdentityBiometricV3;
import com.lulobank.clients.v3.usecase.command.ClientBiometricIdTransactionRequest;

public class IdentityBiometricMapperTest {
	
	@Test
	public void identityBiometricFrom() {
		ClientBiometricIdTransactionRequest clientBiometricIdTransaction = buildClientBiometricIdTransaction();
		IdentityBiometricV3 identityBiometricV3 = IdentityBiometricMapper.INSTANCE.identityBiometricFrom(clientBiometricIdTransaction);
		
		assertThat(identityBiometricV3.getIdTransaction(),is(clientBiometricIdTransaction.getIdTransactionBiometric()));
		assertThat(identityBiometricV3.getStatus(),is(IdentityBiometricStatus.IN_PROGRESS.name()));
	}

	private ClientBiometricIdTransactionRequest buildClientBiometricIdTransaction() {
		ClientBiometricIdTransactionRequest biometricIdTransactionRequest = new ClientBiometricIdTransactionRequest();
		biometricIdTransactionRequest.setIdClient("idClient");
		biometricIdTransactionRequest.setIdTransactionBiometric("idTransactionBiometric");
		return biometricIdTransactionRequest;
	}
}


