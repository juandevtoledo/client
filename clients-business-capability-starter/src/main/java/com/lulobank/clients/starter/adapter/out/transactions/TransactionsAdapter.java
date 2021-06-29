package com.lulobank.clients.starter.adapter.out.transactions;

import com.lulobank.clients.services.application.port.out.transactions.savingsaccounts.TransactionsPort;
import com.lulobank.clients.services.application.port.out.transactions.savingsaccounts.error.TransactionsError;
import com.lulobank.clients.starter.adapter.out.transactions.dto.PendingTransfersDto;
import io.vavr.API;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Collections;
import java.util.Map;

import static com.lulobank.clients.services.application.port.out.transactions.savingsaccounts.error.TransactionsError.connectionError;
import static com.lulobank.clients.services.application.port.out.transactions.savingsaccounts.error.TransactionsErrorStatus.CLI_110;
import static com.lulobank.clients.starter.adapter.out.util.WebClientUtil.mapToHttpHeaders;
import static io.vavr.API.$;
import static io.vavr.API.Case;

@Slf4j
@RequiredArgsConstructor
public class TransactionsAdapter implements TransactionsPort {
    private final WebClient transactionsWebClient;

    public static final String PENDING_TRANSACTIONS = "/transfers/pending/client/{idClient}";
    public static final String ID_CLIENT_PLACEHOLDER = "{idClient}";

    @Override
    public Either<TransactionsError, Boolean> hasPendingTransactions(Map<String, String> headers,
                                                                     String idClient) {
        return Try.of(() -> getPendingTransactions(headers, idClient))
                .map(clientResponse -> Option.of(clientResponse)
                        .filter(clientResponse1 -> !clientResponse1.statusCode().isError())
                        .map(clientResponse1 -> clientResponse.bodyToMono(PendingTransfersDto.class)
                                .block())
                        .map(PendingTransfersDto::getContent)
                        .map(v -> !v.isEmpty())
                        .toEither(() -> getTransactionsErrorFromClientResponse(clientResponse)))
                .recover(t -> Either.left(handleException(t)))
                .get();
    }

    private ClientResponse getPendingTransactions(Map<String, String> headers, String idClient) {
        String path = PENDING_TRANSACTIONS.replace(ID_CLIENT_PLACEHOLDER, idClient);
        return transactionsWebClient
                .get()
                .uri(path, Collections.emptyMap())
                .headers(httpHeaders -> mapToHttpHeaders(headers, httpHeaders))
                .exchange()
                .block();
    }

    private TransactionsError handleException(Throwable t) {
        log.error(CLI_110.getMessage(), t);
        return connectionError();
    }

    private TransactionsError getTransactionsErrorFromClientResponse(ClientResponse clientResponse) {
        return clientResponse.createException()
                .map(this::handleWebClientResponseException)
                .block();
    }

    private TransactionsError handleWebClientResponseException(WebClientResponseException e) {
        log.error("WebClientResponseException when getting pending transactions: {} ", e.getResponseBodyAsString(), e);
        return API.Match(e.getStatusCode()).of(
                Case($(), () -> connectionError(String.valueOf(e.getStatusCode().value())))
        );
    }
}
