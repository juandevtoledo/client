package com.lulobank.clients.starter.adapter.out.reporting;

import com.lulobank.clients.sdk.operations.exception.ClientsServiceException;
import com.lulobank.clients.services.application.port.out.reporting.ReportingPort;
import com.lulobank.clients.services.application.port.out.reporting.model.StoreDigitalEvidenceRequest;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.ClientResponse;

import java.util.Collections;
import java.util.Map;

import static com.lulobank.clients.starter.adapter.out.reporting.ReportingClient.BLACKLISTED_DIGITAL_EVIDENCE;
import static com.lulobank.clients.starter.adapter.out.reporting.ReportingClient.ID_CLIENT_PLACEHOLDER;
import static com.lulobank.clients.starter.adapter.out.util.WebClientUtil.mapToHttpHeaders;

@Slf4j
@RequiredArgsConstructor
public class ReportingAdapter implements ReportingPort {

    public static final String REQUEST_FAILED = "Request failed";
    public static final String CREATE_EVIDENCE_FAILED = "Error consuming service for digital evidence creation. Endpoint clients/%s/digital-evidence. Cause: %s";

    private final ReportingClient reportingClient;

    @Override
    public Try<Boolean> createDigitalEvidence(Map<String, String> headers, String idClient, StoreDigitalEvidenceRequest storeDigitalEvidenceRequest) {
        return Try.of(() -> saveDigitalEvidence(headers, idClient, storeDigitalEvidenceRequest))
                .map(this::processResponse)
                .onFailure(e -> log.error(String.format(CREATE_EVIDENCE_FAILED, idClient, e.getMessage())));
    }

    private Boolean processResponse(ClientResponse response) {
        return Option.of(response)
                .filter(clientResponse -> !clientResponse.statusCode().isError())
                .map(clientResponse -> true)
                .getOrElseThrow(() ->
                        new ClientsServiceException(
                                String.format("STATUS CODE: %s. %s", response.rawStatusCode(), REQUEST_FAILED),
                                response.rawStatusCode()));
    }

    private ClientResponse saveDigitalEvidence(Map<String, String> headers, String idClient, StoreDigitalEvidenceRequest request) {
        String path = BLACKLISTED_DIGITAL_EVIDENCE.replace(ID_CLIENT_PLACEHOLDER, idClient);
        return reportingClient.getWebClient()
                .post()
                .uri(path, Collections.emptyMap())
                .headers(httpHeaders -> mapToHttpHeaders(headers, httpHeaders))
                .bodyValue(request)
                .exchange()
                .block();
    }
}