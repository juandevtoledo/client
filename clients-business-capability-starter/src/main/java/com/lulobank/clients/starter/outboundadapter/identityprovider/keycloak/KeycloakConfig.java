package com.lulobank.clients.starter.outboundadapter.identityprovider.keycloak;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

    @Value("${security.keycloak.url}")
    private String serverURL;

    @Value("${security.keycloak.realm}")
    private String realm;

    @Value("${security.keycloak.user}")
    private String userName;

    @Value("${security.keycloak.password}")
    private String password;

    @Value("${security.keycloak.clientId}")
    private String clientId;

    @Bean
    public KeycloakAdapter getAdapter(){
        return new KeycloakAdapter(getKeyCloakAdmin());
    }

    private RealmResource getKeyCloakAdmin(){
        return KeycloakBuilder.builder()
                .realm(realm)
                .serverUrl(serverURL)
                .clientId(clientId)
                .grantType(OAuth2Constants.PASSWORD)
                .username(userName)
                .password(password)
                .build()
                .realm(realm);

    }
}
