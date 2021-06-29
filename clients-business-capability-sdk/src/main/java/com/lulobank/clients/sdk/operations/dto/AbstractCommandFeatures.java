package com.lulobank.clients.sdk.operations.dto;

import io.vavr.control.Option;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Setter
@Getter
// TODO: mover esta clase a lulo-bank-core cuando se haga el refactor de los handlers
public abstract class AbstractCommandFeatures {
    private static final String AUTHORIZATION_TOKEN_KEY = "authorization";
    private static final String HEADER_FIREBASE = "firebase-id";

    protected Map<String, String> httpHeaders;

    public Map<String, String> getAuthorizationHeader() {
        return Option.of(httpHeaders)
                .map(headersMap -> headersMap.entrySet()
                        .stream()
                        .filter(entry -> AUTHORIZATION_TOKEN_KEY.equalsIgnoreCase(entry.getKey()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
                .getOrElse(new HashMap<>());
    }

    public Map<String, String> getAuthorizationAndFirebaseHeader() {
        return Option.of(httpHeaders)
                .map(headersMap -> headersMap.entrySet()
                        .stream()
                        .filter(entry -> AUTHORIZATION_TOKEN_KEY.equalsIgnoreCase(entry.getKey()) || HEADER_FIREBASE.equalsIgnoreCase(entry.getKey()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
                .getOrElse(new HashMap<>());
    }
}
