package com.lulobank.clients.starter.outboundadapter.authentication;

import com.lulobank.authentication.sdk.dto.InitialClientTokenResponse;
import com.lulobank.authentication.sdk.operations.ClientTokenOperations;
import com.lulobank.clients.services.ports.out.AuthenticationService;
import com.lulobank.clients.starter.outboundadapter.authentication.mapper.AuthenticationServiceMapper;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class AuthenticationServiceAdapter implements AuthenticationService {

    private static final String ERROR_GENERATING_USER_TOKEN = "Error generating initial User Token for new user {}";

    private ClientTokenOperations clientTokenOperations;

    public AuthenticationServiceAdapter(ClientTokenOperations clientTokenOperations) {
        this.clientTokenOperations = clientTokenOperations;
    }

    @Override
    public Try<String> generateTokenUser(Map<String, String> headers, String clientId, ClientsV3Entity client, String password) {
        return Try.of(() -> clientTokenOperations.getClientToken(headers, clientId,
                AuthenticationServiceMapper.INSTANCE.toInitialClientTokenRequest(client, password)))
                .map(InitialClientTokenResponse::getToken)
                .onFailure(exception -> log.error(ERROR_GENERATING_USER_TOKEN, client.getEmailAddress(), exception));
    }
}
