package com.lulobank.clients.starter.outboundadapter.identityprovider.keycloak;

import com.lulobank.clients.services.exception.IdentityProviderException;
import com.lulobank.clients.services.ports.out.IdentityProviderService;
import io.vavr.control.Try;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserRepresentation;

import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class KeycloakAdapter implements IdentityProviderService {

    private static final String ERROR_CREATING_USER = "Problems creating new client %s on Keycloak";

    private RealmResource realmResourceAdmin;

    public KeycloakAdapter( RealmResource realmResourceAdmin) {
        this.realmResourceAdmin = realmResourceAdmin;

    }

    @Override
    public String createUser(String email, String qualityCode, String fistName, String lastName, Map<String, List<String>> attributes) {

        UserRepresentation userRepresentation = new UserRepresentation();
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setType("password");
        credentialRepresentation.setValue(qualityCode);

        userRepresentation.setEmail(email);
        userRepresentation.setFirstName(fistName);
        userRepresentation.setLastName(lastName);
        userRepresentation.setUsername(email);
        userRepresentation.setEnabled(true);
        userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));
        userRepresentation.setAttributes(attributes);

        return Try.of(() -> realmResourceAdmin.users().create(userRepresentation))
                .map(Response::getLocation)
                .map(location -> location.getPath().replaceAll(".*/([^/]+)$", "$1"))
                .getOrElseThrow(e -> new IdentityProviderException(String.format(ERROR_CREATING_USER, email), e));
    }

  @Override
  public void updateUserEmail(String idKeycloak, String newEmail) {
    UserResource userResource = realmResourceAdmin.users().get(idKeycloak);
    UserRepresentation user = userResource.toRepresentation();
    user.setEmail(newEmail);
    user.setUsername(newEmail);
    userResource.update(user);
  }
}
