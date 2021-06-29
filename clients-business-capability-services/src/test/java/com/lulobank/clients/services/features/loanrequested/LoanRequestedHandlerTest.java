package com.lulobank.clients.services.features.loanrequested;

import com.amazonaws.SdkClientException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.lulobank.clients.services.exception.ClientNotFoundException;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.LoanClientRequestedV3;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.LoanRequestedV3;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.OnBoardingStatusV3;
import com.lulobank.core.Response;
import com.lulobank.core.validations.ValidationResult;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static com.lulobank.clients.services.Sample.buildClientsV3Entity;
import static com.lulobank.clients.services.Sample.clientLoanRequestedBuilder;
import static com.lulobank.clients.services.utils.ProductTypeEnum.CREDIT_ACCOUNT;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LoanRequestedHandlerTest {

    private static final String ID_CLIENT = "ebc8ad61-fce8-44a1-9a3c-864d0f608a62";

    @Mock
    private ClientsV3Repository clientsV3Repository;
    private LoanRequestedHandler loanRequestedHandler;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        loanRequestedHandler = new LoanRequestedHandler(clientsV3Repository);
    }

    @Test
    public void shouldGetResponseOkWhenOnBoarding() {
        OnBoardingStatusV3 onBoardingStatusV3 = new OnBoardingStatusV3(null, CREDIT_ACCOUNT.name());
        LoanClientRequestedV3 loanClientRequested = new LoanClientRequestedV3();
        loanClientRequested.setAmount(10000000d);
        loanClientRequested.setLoanPurpose("Trip");
        ClientsV3Entity clientsV3Entity = buildClientsV3Entity(onBoardingStatusV3, null);
        clientsV3Entity.getOnBoardingStatus().setLoanClientRequested(loanClientRequested);
        when(clientsV3Repository.findByIdClient(ID_CLIENT)).thenReturn(Option.of(buildClientsV3Entity(onBoardingStatusV3, null)));
        when(clientsV3Repository.save(refEq(clientsV3Entity))).thenReturn(Try.of(()->clientsV3Entity));

        Response response = loanRequestedHandler.handle(clientLoanRequestedBuilder(ID_CLIENT, 10000000d, "Trip"));

        assertThat(response, notNullValue());
        assertThat(response.getHasErrors(), is(false));
        assertThat(response.getContent(), notNullValue());
        ClientEntity content = (ClientEntity) response.getContent();
        assertThat(content.getOnBoardingStatus().getLoanClientRequested(), notNullValue());
        assertThat(content.getOnBoardingStatus().getLoanClientRequested().getAmount(), is(10000000d));
        assertThat(content.getOnBoardingStatus().getLoanClientRequested().getLoanPurpose(), is("Trip"));


        verify(clientsV3Repository).save(refEq(clientsV3Entity));
    }

    @Test
    public void shouldGetResponseOkWhenRequestedFromHomeAndOnBoardingNull() {
        LoanRequestedV3 requestedV3 = new LoanRequestedV3();

        LoanClientRequestedV3 loanClientRequested = new LoanClientRequestedV3();
        loanClientRequested.setAmount(10000000d);
        loanClientRequested.setLoanPurpose("Trip");
        ClientsV3Entity clientsV3Entity = buildClientsV3Entity(null, requestedV3);
        clientsV3Entity.getLoanRequested().setLoanClientRequested(loanClientRequested);
        when(clientsV3Repository.findByIdClient(ID_CLIENT)).thenReturn(Option.of(buildClientsV3Entity(null, requestedV3)));
        when(clientsV3Repository.save(refEq(clientsV3Entity))).thenReturn(Try.of(()->clientsV3Entity));

        Response response = loanRequestedHandler.handle(clientLoanRequestedBuilder(ID_CLIENT, 10000000d, "Trip"));

        assertThat(response, notNullValue());
        assertThat(response.getHasErrors(), is(false));
        assertThat(response.getContent(), notNullValue());
        ClientEntity content = (ClientEntity) response.getContent();
        assertThat(content.getLoanRequested().getLoanClientRequested(), notNullValue());
        assertThat(content.getLoanRequested().getLoanClientRequested().getAmount(), is(10000000d));
        assertThat(content.getLoanRequested().getLoanClientRequested().getLoanPurpose(), is("Trip"));

        verify(clientsV3Repository).save(refEq(clientsV3Entity));    }

    @Test
    public void shouldGetResponseOkWhenRequestedFromHomeAndOnBoardingNotNull() {
        LoanRequestedV3 requestedV3 = new LoanRequestedV3();
        OnBoardingStatusV3 onBoardingStatusV3 = new OnBoardingStatusV3();
        LoanClientRequestedV3 loanClientRequested = new LoanClientRequestedV3();
        loanClientRequested.setAmount(10000000d);
        loanClientRequested.setLoanPurpose("Trip");
        ClientsV3Entity clientsV3Entity = buildClientsV3Entity(onBoardingStatusV3, requestedV3);
        clientsV3Entity.getLoanRequested().setLoanClientRequested(loanClientRequested);
        when(clientsV3Repository.findByIdClient(ID_CLIENT)).thenReturn(Option.of(buildClientsV3Entity(onBoardingStatusV3, requestedV3)));
        when(clientsV3Repository.save(refEq(clientsV3Entity))).thenReturn(Try.of(()->clientsV3Entity));

        Response response = loanRequestedHandler.handle(clientLoanRequestedBuilder(ID_CLIENT, 10000000d, "Trip"));

        assertThat(response, notNullValue());
        assertThat(response.getHasErrors(), is(false));
        assertThat(response.getContent(), notNullValue());
        ClientEntity content = (ClientEntity) response.getContent();
        assertThat(content.getLoanRequested().getLoanClientRequested(), notNullValue());
        assertThat(content.getLoanRequested().getLoanClientRequested().getAmount(), is(10000000d));
        assertThat(content.getLoanRequested().getLoanClientRequested().getLoanPurpose(), is("Trip"));
        verify(clientsV3Repository).save(refEq(clientsV3Entity));
    }

    @Test(expected = ClientNotFoundException.class)
    public void shouldGetResponseWhenClientDoesNotExists() {
        when(clientsV3Repository.findByIdClient(ID_CLIENT)).thenReturn(Option.none());

        loanRequestedHandler.handle(clientLoanRequestedBuilder(ID_CLIENT, 10000000d, "Trip"));
    }

    @Test
    public void shouldGetResponseWhenProviderException() {
        LoanRequestedV3 requestedV3 = new LoanRequestedV3();
        OnBoardingStatusV3 onBoardingStatusV3 = new OnBoardingStatusV3();

        when(clientsV3Repository.findByIdClient(ID_CLIENT)).thenReturn(Option.of(buildClientsV3Entity(onBoardingStatusV3, requestedV3)));
        LoanClientRequestedV3 loanClientRequested = new LoanClientRequestedV3();
        loanClientRequested.setAmount(10000000d);
        loanClientRequested.setLoanPurpose("Trip");
        ClientsV3Entity clientsV3Entity = buildClientsV3Entity(onBoardingStatusV3, requestedV3);
        clientsV3Entity.getLoanRequested().setLoanClientRequested(loanClientRequested);
        when(clientsV3Repository.save(refEq(clientsV3Entity))).thenReturn(Try.failure(new SdkClientException("")));

        Response response = loanRequestedHandler.handle(clientLoanRequestedBuilder(ID_CLIENT, 10000000d, "Trip"));

        assertThat(response, notNullValue());
        assertThat(response.getHasErrors(), is(true));
        assertThat(response.getContent(), nullValue());
        ValidationResult result = (ValidationResult) response.getErrors().get(0);
        assertThat(result.getFailure(), is("INTERNAL_SERVER_ERROR"));
        assertThat(result.getValue(), is("500"));

        verify(clientsV3Repository).save(refEq(clientsV3Entity));
    }
}