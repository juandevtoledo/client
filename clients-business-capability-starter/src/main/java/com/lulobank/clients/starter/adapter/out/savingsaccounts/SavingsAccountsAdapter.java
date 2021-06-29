package com.lulobank.clients.starter.adapter.out.savingsaccounts;

import com.lulobank.clients.services.application.port.out.savingsaccounts.SavingsAccountsPort;
import com.lulobank.clients.services.application.port.out.savingsaccounts.error.SavingsAccountsError;
import com.lulobank.clients.services.application.port.out.savingsaccounts.model.SavingAccount;
import com.lulobank.clients.starter.adapter.out.savingsaccounts.dto.ResponseSavingAccountType;
import com.lulobank.clients.starter.adapter.out.savingsaccounts.mapper.SavingsAccountsMapper;
import io.vavr.API;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Collections;
import java.util.Map;

import static com.lulobank.clients.services.application.port.out.savingsaccounts.error.SavingsAccountsError.connectionError;
import static com.lulobank.clients.services.application.port.out.savingsaccounts.error.SavingsAccountsErrorStatus.CLI_110;
import static com.lulobank.clients.starter.adapter.out.savingsaccounts.SavingsAccountsClient.ACCOUNT_BY_CLIENT_ZENDESK;
import static com.lulobank.clients.starter.adapter.out.savingsaccounts.SavingsAccountsClient.ID_CLIENT_PLACEHOLDER;
import static com.lulobank.clients.starter.adapter.out.util.WebClientUtil.mapToHttpHeaders;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.isIn;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
public class SavingsAccountsAdapter implements SavingsAccountsPort {
    private final SavingsAccountsClient savingsAccountsClient;

    @Override
    public Either<SavingsAccountsError, SavingAccount> getSavingsAccountsByIdClient(Map<String, String> headers,
                                                                                    String idClient) {
        return Try.of(() -> getSavingsAccountClient(headers, idClient))
                .map(clientResponse -> Option.of(clientResponse)
                        .filter(clientResponse1 -> !clientResponse1.statusCode().isError())
                        .map(clientResponse1 -> clientResponse.bodyToMono(ResponseSavingAccountType.class)
                                .block())
                        .map(ResponseSavingAccountType::getContent)
                        .map(SavingsAccountsMapper.INSTANCE::toDomain)
                        .toEither(() -> getSavingsAccountsErrorFromClientResponse(clientResponse)))
                .recover(t -> Either.left(handleException(t)))
                .get();
    }

    private ClientResponse getSavingsAccountClient(Map<String, String> headers, String idClient) {
        String path = ACCOUNT_BY_CLIENT_ZENDESK.replace(ID_CLIENT_PLACEHOLDER, idClient);
        return savingsAccountsClient.getWebClient()
                .get()
                .uri(path, Collections.emptyMap())
                .headers(httpHeaders -> mapToHttpHeaders(headers, httpHeaders))
                .exchange()
                .block();
    }
    private SavingsAccountsError handleException(Throwable t) {
        log.error(CLI_110.getMessage(), t);
        return connectionError();
    }
    private SavingsAccountsError getSavingsAccountsErrorFromClientResponse(ClientResponse clientResponse) {
        return clientResponse.createException()
                .map(this::handleWebClientResponseException)
                .block();
    }

    private SavingsAccountsError handleWebClientResponseException(WebClientResponseException e) {
        log.error("WebClientResponseException when getting savingsAccount by idClient: {} ", e.getResponseBodyAsString(), e);
        return API.Match(e.getStatusCode()).of(
                Case($(isIn(NOT_FOUND)), SavingsAccountsError::accountNotFound),
                Case($(), () -> connectionError(String.valueOf(e.getStatusCode().value())))
        );
    }
}
