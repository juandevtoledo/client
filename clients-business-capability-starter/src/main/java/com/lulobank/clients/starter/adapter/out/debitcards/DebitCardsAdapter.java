package com.lulobank.clients.starter.adapter.out.debitcards;

import com.lulobank.clients.services.application.port.out.debitcards.DebitCardsPort;
import com.lulobank.clients.services.application.port.out.debitcards.error.DebitCardsError;
import com.lulobank.clients.services.application.port.out.debitcards.model.CardStatus;
import com.lulobank.clients.services.application.port.out.debitcards.model.DebitCard;
import com.lulobank.clients.starter.adapter.out.debitcards.dto.ResponseDebitCardInformation;
import com.lulobank.clients.starter.adapter.out.debitcards.dto.ResponseDebitCardStatus;
import com.lulobank.clients.starter.adapter.out.debitcards.mapper.DebitCardsMapper;
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

import static com.lulobank.clients.services.application.port.out.debitcards.error.DebitCardsError.connectionError;
import static com.lulobank.clients.services.application.port.out.debitcards.error.DebitCardsErrorStatus.CLI_115;
import static com.lulobank.clients.starter.adapter.out.debitcards.DebitCardsClient.CARD_BY_CLIENT;
import static com.lulobank.clients.starter.adapter.out.debitcards.DebitCardsClient.CARD_STATUS_BY_CLIENT;
import static com.lulobank.clients.starter.adapter.out.debitcards.DebitCardsClient.ID_CLIENT_PLACEHOLDER;
import static com.lulobank.clients.starter.adapter.out.util.WebClientUtil.mapToHttpHeaders;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.isIn;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
public class DebitCardsAdapter implements DebitCardsPort {

    private final DebitCardsClient debitCardsClient;

    @Override
    public Either<DebitCardsError, DebitCard> getDebitCardByIdClient(Map<String, String> headers,
                                                                     String idClient) {
        return Try.of(() -> getDebitCardClient(headers, idClient))
                .map(clientResponse -> Option.of(clientResponse)
                        .filter(clientResponse1 -> !clientResponse1.statusCode().isError())
                        .map(clientResponse1 -> clientResponse.bodyToMono(ResponseDebitCardInformation.class)
                                .block())
                        .map(ResponseDebitCardInformation::getContent)
                        .map(DebitCardsMapper.INSTANCE::toDomain)
                        .toEither(() -> getDebitCardClientErrorFromClientResponse(clientResponse)))
                .recover(t -> Either.left(handleException(t)))
                .get();
    }



    @Override
    public Either<DebitCardsError, CardStatus> getDebitCardStatusByIdClient(Map<String, String> headers, String idClient) {
        return Try.of(() -> getDebitCardStatusClient(headers, idClient))
                .map(clientResponse -> Option.of(clientResponse)
                        .filter(clientResponse1 -> !clientResponse1.statusCode().isError())
                        .map(clientResponse1 -> clientResponse.bodyToMono(ResponseDebitCardStatus.class)
                                .block())
                        .map(ResponseDebitCardStatus::getContent)
                        .map(DebitCardsMapper.INSTANCE::toDomain)
                        .toEither(() -> getDebitCardClientErrorFromClientResponse(clientResponse)))
                .recover(t -> Either.left(handleException(t)))
                .get();
    }

    private ClientResponse getDebitCardClient(Map<String, String> headers, String idClient) {
        String path = CARD_BY_CLIENT.replace(ID_CLIENT_PLACEHOLDER, idClient);
        return debitCardsClient.getWebClient()
                .get()
                .uri(path, Collections.emptyMap())
                .headers(httpHeaders -> mapToHttpHeaders(headers, httpHeaders))
                .exchange()
                .block();
    }

    private ClientResponse getDebitCardStatusClient(Map<String, String> headers, String idClient) {
        String path = CARD_STATUS_BY_CLIENT.replace(ID_CLIENT_PLACEHOLDER, idClient);
        return debitCardsClient.getWebClient()
                .get()
                .uri(path, Collections.emptyMap())
                .headers(httpHeaders -> mapToHttpHeaders(headers, httpHeaders))
                .exchange()
                .block();
    }

    private DebitCardsError getDebitCardClientErrorFromClientResponse(ClientResponse clientResponse) {
        return clientResponse.createException()
                .map(this::handleWebClientResponseException)
                .block();
    }

    private DebitCardsError handleWebClientResponseException(WebClientResponseException e) {
        log.error("WebClientResponseException when getting cards info by idClient: {} ", e.getResponseBodyAsString(), e);
        return API.Match(e.getStatusCode()).of(
                Case($(isIn(NOT_FOUND)), DebitCardsError::cardNotFound),
                Case($(), () -> connectionError(String.valueOf(e.getStatusCode().value())))
        );
    }

    private DebitCardsError handleException(Throwable t) {
        log.error(CLI_115.getMessage(), t);
        return DebitCardsError.connectionError();
    }
}
