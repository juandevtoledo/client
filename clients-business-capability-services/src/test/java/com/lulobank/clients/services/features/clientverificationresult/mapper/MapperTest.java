package com.lulobank.clients.services.features.clientverificationresult.mapper;

import com.lulobank.clients.services.Sample;
import com.lulobank.clients.services.events.ClientPersonalInformationResult;
import com.lulobank.clients.services.events.ClientVerificationResult;
import com.lulobank.clients.services.events.IdDocument;
import com.lulobank.clients.services.events.IdentityInformation;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.IdentityBiometricV3;
import com.lulobank.savingsaccounts.sdk.dto.createsavingsaccount.ClientInformation;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.Objects;

import static com.lulobank.clients.services.Constants.BIRTH_DATE_ENTITY;
import static com.lulobank.clients.services.Constants.DATE_ISSUE;
import static com.lulobank.clients.services.Constants.DATE_ISSUE_ENTITY;
import static com.lulobank.clients.services.Constants.EMAIL;
import static com.lulobank.clients.services.Constants.GENDER;
import static com.lulobank.clients.services.Constants.ID_CARD;
import static com.lulobank.clients.services.Constants.ID_CLIENT;
import static com.lulobank.clients.services.Constants.ID_TRANSACTION;
import static com.lulobank.clients.services.Constants.LAST_NAME;
import static com.lulobank.clients.services.Constants.NAME;
import static com.lulobank.clients.services.Constants.PHONE;
import static com.lulobank.clients.services.Constants.PREFIX;
import static com.lulobank.clients.services.SamplesV3.clientEntityV3Builder;
import static com.lulobank.clients.services.domain.DocumentType.CC;
import static com.lulobank.clients.services.utils.BiometricResultCodes.SUCCESSFUL;
import static com.lulobank.clients.services.utils.IdentityBiometricStatus.FINISHED;
import static com.lulobank.clients.services.utils.IdentityBiometricStatus.IN_PROGRESS;
import static flexibility.client.models.Gender.M;
import static joptsimple.internal.Strings.EMPTY;
import static org.eclipse.jetty.util.StringUtil.valueOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.isNull;

public class MapperTest {

    private ClientEntityMapper clientEntityMapperTest = ClientEntityMapper.INSTANCE;
    private IdentityBiometricMapper identityBiometricMapper = IdentityBiometricMapper.INSTANCE;
    private ClientInformationMapper clientInformationMapper = ClientInformationMapper.INSTANCE;
    private IdentityInformationMapper identityInformationMapper = IdentityInformationMapper.INSTANCE;

    @Test
    public void shouldMapClientEntity() {
        ClientsV3Entity clientEntity = clientEntityV3Builder();
        ClientVerificationResult clientVerificationResult = new ClientVerificationResult();
        IdDocument idDocument = Sample.idDocumentBuilder(ID_CARD, DATE_ISSUE);
        ClientPersonalInformationResult clientPersonalInformationResult = Sample
                .clientPersonalInformationResultBuilder(NAME, LAST_NAME, GENDER, idDocument);
        clientVerificationResult.setClientPersonalInformation(clientPersonalInformationResult);
        ClientsV3Entity mapper = clientEntityMapperTest.clientEntityFrom(clientVerificationResult, clientEntity);
        assertEquals(ID_CLIENT, mapper.getIdClient());
        assertEquals(NAME, mapper.getName());
        assertEquals(FINISHED.name(), mapper.getIdentityBiometric().getStatus());
        assertEquals(GENDER, mapper.getGender());
        assertEquals(LAST_NAME, mapper.getLastName());
        assertNull(mapper.getAdditionalPersonalInformation().getSecondName());
    }

    @Test
    public void shouldMapIdentityBiometric() {
        ClientVerificationResult clientVerificationResult = new ClientVerificationResult();
        clientVerificationResult.setIdTransactionBiometric(ID_TRANSACTION);
        clientVerificationResult.setTransactionState(Sample.transactionStateEventBuilder(SUCCESSFUL));
        IdentityBiometricV3 mapper = identityBiometricMapper.identityBiometricFrom(clientVerificationResult);
        assertEquals(IN_PROGRESS.name(), mapper.getStatus());
        assertEquals(ID_TRANSACTION, mapper.getIdTransaction());
        assertTrue(Objects.isNull(mapper.getTransactionState()));
    }

    @Test
    public void shouldMapClientInformation() {
        ClientsV3Entity clientEntity = clientEntityV3Builder();
        ClientInformation mapper = clientInformationMapper.clientInformationFrom(clientEntity);
        assertEquals(ID_CARD, mapper.getDocumentId().getId());
    }

    @Test
    public void shouldMapIdentityInformation() {
        ClientsV3Entity clientEntity = clientEntityV3Builder();
        IdentityInformation mapper = identityInformationMapper.identityInformationFromClient(clientEntity);
        assertEquals(ID_CARD, mapper.getDocumentNumber());
        assertEquals(DATE_ISSUE_ENTITY, mapper.getExpeditionDate());
        assertEquals(CC.name(), mapper.getDocumentType());
        assertEquals(BIRTH_DATE_ENTITY, mapper.getBirthDate());
        assertEquals(NAME, mapper.getName());
        assertEquals(LAST_NAME, mapper.getLastName());
        assertEquals(M.getValue(), mapper.getGender().toLowerCase());
        assertEquals(EMAIL, mapper.getEmail());
        assertEquals(PHONE, mapper.getPhone().getNumber());
        assertEquals(valueOf(PREFIX), mapper.getPhone().getPrefix());

    }
}
