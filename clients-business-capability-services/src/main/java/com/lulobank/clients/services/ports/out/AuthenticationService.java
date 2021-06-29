package com.lulobank.clients.services.ports.out;

import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import io.vavr.control.Try;

import java.util.Map;

public interface AuthenticationService {

    Try<String> generateTokenUser(Map<String, String> headers, String clientId, ClientsV3Entity client, String password);

}
