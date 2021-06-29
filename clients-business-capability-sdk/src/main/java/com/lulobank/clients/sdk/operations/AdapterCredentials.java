package com.lulobank.clients.sdk.operations;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class AdapterCredentials {

    private static final String AUTHORIZATION_HEADER = "authorization";
    private final Map<String, String> headers;

    public AdapterCredentials(Map<String, String> headers) {
        this.headers = getAuthorizationHeaders(headers);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    private Map<String, String> getAuthorizationHeaders(Map<String, String> headers) {
        return headers.entrySet().stream()
                .filter(entry -> AUTHORIZATION_HEADER.equalsIgnoreCase(entry.getKey()))
                .findFirst()
                .map(entry -> Collections.singletonMap(entry.getKey(), entry.getValue()))
                .orElse(Collections.emptyMap());
    }

    public Map<String, Object> getAuthorizationHeadersToSqs() {
        return headers.entrySet().stream()
                .filter(entry -> AUTHORIZATION_HEADER.equalsIgnoreCase(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));


    }
}
