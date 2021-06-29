package com.lulobank.clients.starter.outboundadapter.customerservice;

import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.services.exception.CreateCustomerException;
import com.lulobank.clients.services.ports.out.CustomerService;
import com.lulobank.clients.services.ports.out.error.CustomerServiceError;
import com.lulobank.clients.starter.outboundadapter.customerservice.dto.CreateCustomerRequest;
import com.lulobank.clients.starter.outboundadapter.customerservice.mapper.CustomerServiceAdapterMapper;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

import java.util.Collections;
import java.util.Map;

import static com.lulobank.clients.starter.adapter.out.util.WebClientUtil.mapToHttpHeaders;
import static com.lulobank.clients.starter.outboundadapter.customerservice.CustomerServiceClient.CREATE_USER_CUSTOMER;

@Slf4j
@RequiredArgsConstructor
public class CustomerServiceAdapter implements CustomerService {

    private static final String ERROR_CUSTOMER_SERVICE_CREATION = "Error creating client in customer service %s";
    private static final String ERROR_CONNECTION_CUSTOMER_SERVICE = "Error connecting customer service. Error {}";

    private final CustomerServiceClient customerServiceClient;

    @Override
    public Try<Boolean> createUserCustomer(Map<String, String> headers, ClientsV3Entity client) {
        return Try.of(() -> createCustomer(headers, CustomerServiceAdapterMapper.INSTANCE.toCreateCustomerRequest(client)))
                .map(clientResponse -> Option.of(clientResponse)
                        .filter(clientResponse1 -> !clientResponse1.statusCode().isError())
                        .map(response -> true)
                        .toTry(() -> new CreateCustomerException(String.format(ERROR_CUSTOMER_SERVICE_CREATION,
                                client.getIdCard()))))
                .recover(t -> handleException(t, client.getIdCard()))
                .get();
    }

    @Override
    public Either<UseCaseResponseError, Boolean> isEmailExist(Map<String, String> headers, String email) {
        HttpStatus statusCode = getCustomerByEmail(headers, email)
                                    .map(ClientResponse::statusCode)
                                    .fold(() -> HttpStatus.INTERNAL_SERVER_ERROR, r -> r);
        return Match(statusCode.value())
                .of(Case($(HttpStatus.OK.value()), customer -> Either.right(true)),
                        Case($(HttpStatus.NOT_FOUND.value()), e -> Either.right(false)),
                        Case($(), e -> Either.left(CustomerServiceError.connectionError()))
                );
    }

    private Option<ClientResponse> getCustomerByEmail(Map<String, String> headers, String email) {
        return Option.of(customerServiceClient.getWebClient()
                .get()
                .uri(uriBuilder -> uriBuilder.path(CREATE_USER_CUSTOMER).queryParam("emailAddress", email).build())
                .headers(httpHeaders -> mapToHttpHeaders(headers, httpHeaders))
                .exchange()
                .block());
    }

    private ClientResponse createCustomer(Map<String, String> headers, CreateCustomerRequest createCustomerRequest) {
        return customerServiceClient.getWebClient()
                .post()
                .uri(CREATE_USER_CUSTOMER, Collections.emptyMap())
                .body(Mono.just(createCustomerRequest), CreateCustomerRequest.class)
                .headers(httpHeaders -> mapToHttpHeaders(headers, httpHeaders))
                .exchange()
                .block();
    }

    private Try<Boolean> handleException(Throwable t, String idCard) {
        log.error(ERROR_CONNECTION_CUSTOMER_SERVICE, t.getMessage(), t);
        return Try.failure(new CreateCustomerException(String.format(ERROR_CUSTOMER_SERVICE_CREATION,
                idCard)));
    }
}
