package com.lulobank.clients.services.application.usecase;

import com.lulobank.clients.services.application.BaseUnitTest;
import com.lulobank.clients.services.application.Sample;
import com.lulobank.clients.v3.error.ClientsDataError;
import com.lulobank.clients.v3.error.ClientsDataErrorStatus;
import com.lulobank.clients.services.application.port.out.debitcards.error.DebitCardsError;
import com.lulobank.clients.services.application.port.out.debitcards.error.DebitCardsErrorStatus;
import com.lulobank.clients.services.application.port.out.debitcards.model.CardStatus;
import com.lulobank.clients.services.application.port.out.savingsaccounts.error.SavingsAccountsError;
import com.lulobank.clients.services.application.port.out.savingsaccounts.error.SavingsAccountsErrorStatus;
import com.lulobank.clients.services.application.usecase.zendeskclientinfo.ZendeskClientInfoUseCase;
import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.services.domain.zendeskclientinfo.GetClientInfoByEmailRequest;
import com.lulobank.clients.services.domain.zendeskclientinfo.GetClientInfoByEmailResponse;
import io.vavr.control.Either;
import org.junit.Test;
import org.mockito.InjectMocks;

import static com.lulobank.clients.services.application.Constant.ID_CARD;
import static com.lulobank.clients.services.application.Constant.ID_CLIENT;
import static com.lulobank.clients.services.application.Constant.ID_SAVINGS_ACCOUNT;
import static com.lulobank.clients.services.application.Constant.MAIL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ZendeskClientInfoUseCaseTest extends BaseUnitTest {
    @InjectMocks
    private ZendeskClientInfoUseCase zendeskClientInfoUseCase;

    @Test
    public void shouldReturnClientInfo() {
        when(clientsDataRepositoryPort.findByEmailAddress(emailCaptor.capture()))
                .thenReturn(Either.right(Sample.getClientsV3EntityZendesk()));
        when(savingsAccountsPort.getSavingsAccountsByIdClient(headersCaptor.capture(), idClientCaptor.capture()))
                .thenReturn(Either.right(Sample.getSavingAccount()));
        when(debitCardsPort.getDebitCardByIdClient(headersCaptor.capture(), idClientCaptor.capture()))
                .thenReturn(Either.right(Sample.getDebitCard()));
        when(debitCardsPort.getDebitCardStatusByIdClient(headersCaptor.capture(), idClientCaptor.capture()))
                .thenReturn(Either.right(Sample.getDebitCardStatus()));

        GetClientInfoByEmailRequest request = Sample.getClientInfoByEmailRequest();
        Either<UseCaseResponseError, GetClientInfoByEmailResponse> response =
                zendeskClientInfoUseCase.execute(request);

        assertEquals(request.getEmail(), emailCaptor.getValue());
        assertEquals(request.getAuthorizationHeader(), headersCaptor.getAllValues().get(0));
        assertEquals(request.getAuthorizationHeader(), headersCaptor.getAllValues().get(1));
        assertEquals(request.getAuthorizationHeader(), headersCaptor.getAllValues().get(2));
        assertEquals(ID_CLIENT, idClientCaptor.getAllValues().get(0));
        assertEquals(ID_CLIENT, idClientCaptor.getAllValues().get(1));
        assertEquals(ID_CLIENT, idClientCaptor.getAllValues().get(2));
        assertEquals(ID_CLIENT, response.get().getIdClient());
        assertEquals(MAIL, response.get().getCustomer().getEmail());
        assertEquals(ID_CARD, response.get().getCustomer().getDocumentNumber());
        assertEquals(2, response.get().getProducts().size());
        assertTrue(ID_SAVINGS_ACCOUNT.endsWith(response.get().getProducts().get(0).getProductNumber()));
        assertTrue(ID_CARD.endsWith(response.get().getProducts().get(1).getProductNumber()));
    }

    @Test
    public void shouldReturnClientInfoCardFreeze() {
        when(clientsDataRepositoryPort.findByEmailAddress(emailCaptor.capture()))
                .thenReturn(Either.right(Sample.getClientsV3EntityZendesk()));
        when(savingsAccountsPort.getSavingsAccountsByIdClient(headersCaptor.capture(), idClientCaptor.capture()))
                .thenReturn(Either.right(Sample.getSavingAccount()));
        when(debitCardsPort.getDebitCardByIdClient(headersCaptor.capture(), idClientCaptor.capture()))
                .thenReturn(Either.right(Sample.getDebitCard()));
        CardStatus cardStatus = Sample.getDebitCardStatus();
        cardStatus.setStatus("FREEZE");
        when(debitCardsPort.getDebitCardStatusByIdClient(headersCaptor.capture(), idClientCaptor.capture()))
                .thenReturn(Either.right(cardStatus));

        GetClientInfoByEmailRequest request = Sample.getClientInfoByEmailRequest();
        Either<UseCaseResponseError, GetClientInfoByEmailResponse> response =
                zendeskClientInfoUseCase.execute(request);

        assertEquals(request.getEmail(), emailCaptor.getValue());
        assertEquals(request.getAuthorizationHeader(), headersCaptor.getAllValues().get(0));
        assertEquals(request.getAuthorizationHeader(), headersCaptor.getAllValues().get(1));
        assertEquals(request.getAuthorizationHeader(), headersCaptor.getAllValues().get(2));
        assertEquals(ID_CLIENT, idClientCaptor.getAllValues().get(0));
        assertEquals(ID_CLIENT, idClientCaptor.getAllValues().get(1));
        assertEquals(ID_CLIENT, idClientCaptor.getAllValues().get(2));
        assertEquals(ID_CLIENT, response.get().getIdClient());
        assertEquals(MAIL, response.get().getCustomer().getEmail());
        assertEquals(ID_CARD, response.get().getCustomer().getDocumentNumber());
        assertEquals(1, response.get().getProducts().size());
        assertTrue(ID_SAVINGS_ACCOUNT.endsWith(response.get().getProducts().get(0).getProductNumber()));
    }

    @Test
    public void shouldReturnErrorDueRepositoryError() {
        when(clientsDataRepositoryPort.findByEmailAddress(emailCaptor.capture()))
                .thenReturn(Either.left(ClientsDataError.connectionFailure()));

        GetClientInfoByEmailRequest request = Sample.getClientInfoByEmailRequest();
        Either<UseCaseResponseError, GetClientInfoByEmailResponse> response =
                zendeskClientInfoUseCase.execute(request);

        assertEquals(request.getEmail(), emailCaptor.getValue());
        assertEquals(ClientsDataErrorStatus.CLI_100.name(),response.getLeft().getBusinessCode());
    }

    @Test
    public void shouldReturnErrorDueRepositoryNotFound() {
        when(clientsDataRepositoryPort.findByEmailAddress(emailCaptor.capture()))
                .thenReturn(Either.left(ClientsDataError.clientNotFound()));

        GetClientInfoByEmailRequest request = Sample.getClientInfoByEmailRequest();
        Either<UseCaseResponseError, GetClientInfoByEmailResponse> response =
                zendeskClientInfoUseCase.execute(request);

        assertEquals(request.getEmail(), emailCaptor.getValue());
        assertEquals(ClientsDataErrorStatus.CLI_101.name(),response.getLeft().getBusinessCode());
    }

    @Test
    public void shouldReturnErrorDueSavingAccountsError() {
        when(clientsDataRepositoryPort.findByEmailAddress(emailCaptor.capture()))
                .thenReturn(Either.right(Sample.getClientsV3EntityZendesk()));
        when(savingsAccountsPort.getSavingsAccountsByIdClient(headersCaptor.capture(), idClientCaptor.capture()))
                .thenReturn(Either.left(SavingsAccountsError.connectionError()));

        GetClientInfoByEmailRequest request = Sample.getClientInfoByEmailRequest();
        Either<UseCaseResponseError, GetClientInfoByEmailResponse> response =
                zendeskClientInfoUseCase.execute(request);

        assertEquals(request.getEmail(), emailCaptor.getValue());
        assertEquals(request.getAuthorizationHeader(), headersCaptor.getAllValues().get(0));
        assertEquals(ID_CLIENT, idClientCaptor.getAllValues().get(0));
        assertEquals(SavingsAccountsErrorStatus.CLI_110.name(),response.getLeft().getBusinessCode());

    }

    @Test
    public void shouldReturnErrorDueSavingAccountsNotFound() {
        when(clientsDataRepositoryPort.findByEmailAddress(emailCaptor.capture()))
                .thenReturn(Either.right(Sample.getClientsV3EntityZendesk()));
        when(savingsAccountsPort.getSavingsAccountsByIdClient(headersCaptor.capture(), idClientCaptor.capture()))
                .thenReturn(Either.left(SavingsAccountsError.accountNotFound()));

        GetClientInfoByEmailRequest request = Sample.getClientInfoByEmailRequest();
        Either<UseCaseResponseError, GetClientInfoByEmailResponse> response =
                zendeskClientInfoUseCase.execute(request);

        assertEquals(request.getEmail(), emailCaptor.getValue());
        assertEquals(request.getAuthorizationHeader(), headersCaptor.getAllValues().get(0));
        assertEquals(ID_CLIENT, idClientCaptor.getAllValues().get(0));
        assertEquals(SavingsAccountsErrorStatus.CLI_111.name(),response.getLeft().getBusinessCode());

    }

    @Test
    public void shouldReturnErrorDueDebitCardConnectionError() {
        when(clientsDataRepositoryPort.findByEmailAddress(emailCaptor.capture()))
                .thenReturn(Either.right(Sample.getClientsV3EntityZendesk()));
        when(savingsAccountsPort.getSavingsAccountsByIdClient(headersCaptor.capture(), idClientCaptor.capture()))
                .thenReturn(Either.right(Sample.getSavingAccount()));
        when(debitCardsPort.getDebitCardByIdClient(headersCaptor.capture(), idClientCaptor.capture()))
                .thenReturn(Either.left(DebitCardsError.connectionError()));

        GetClientInfoByEmailRequest request = Sample.getClientInfoByEmailRequest();
        Either<UseCaseResponseError, GetClientInfoByEmailResponse> response =
                zendeskClientInfoUseCase.execute(request);

        assertEquals(request.getEmail(), emailCaptor.getValue());
        assertEquals(request.getAuthorizationHeader(), headersCaptor.getAllValues().get(0));
        assertEquals(request.getAuthorizationHeader(), headersCaptor.getAllValues().get(1));
        assertEquals(ID_CLIENT, idClientCaptor.getAllValues().get(0));
        assertEquals(ID_CLIENT, idClientCaptor.getAllValues().get(1));
        assertEquals(DebitCardsErrorStatus.CLI_115.name(),response.getLeft().getBusinessCode());
    }

    @Test
    public void shouldReturnErrorDueDebitCardNotFound() {
        when(clientsDataRepositoryPort.findByEmailAddress(emailCaptor.capture()))
                .thenReturn(Either.right(Sample.getClientsV3EntityZendesk()));
        when(savingsAccountsPort.getSavingsAccountsByIdClient(headersCaptor.capture(), idClientCaptor.capture()))
                .thenReturn(Either.right(Sample.getSavingAccount()));
        when(debitCardsPort.getDebitCardByIdClient(headersCaptor.capture(), idClientCaptor.capture()))
                .thenReturn(Either.left(DebitCardsError.cardNotFound()));

        GetClientInfoByEmailRequest request = Sample.getClientInfoByEmailRequest();
        Either<UseCaseResponseError, GetClientInfoByEmailResponse> response =
                zendeskClientInfoUseCase.execute(request);

        assertEquals(request.getEmail(), emailCaptor.getValue());
        assertEquals(request.getAuthorizationHeader(), headersCaptor.getAllValues().get(0));
        assertEquals(request.getAuthorizationHeader(), headersCaptor.getAllValues().get(1));
        assertEquals(ID_CLIENT, idClientCaptor.getAllValues().get(0));
        assertEquals(ID_CLIENT, idClientCaptor.getAllValues().get(1));
        assertEquals(DebitCardsErrorStatus.CLI_116.name(),response.getLeft().getBusinessCode());
    }
}
