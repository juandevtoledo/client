package com.lulobank.clients.services.ports.out;

import java.util.List;
import java.util.Map;

public interface IdentityProviderService {

    String createUser(String email, String qualityCode, String fistName, String lastName, Map<String, List<String>> attributes);

    void updateUserEmail(String idKeycloak, String newEmail);
}
