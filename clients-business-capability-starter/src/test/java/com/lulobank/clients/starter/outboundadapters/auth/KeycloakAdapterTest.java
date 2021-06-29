package com.lulobank.clients.starter.outboundadapters.auth;

import com.lulobank.clients.starter.outboundadapter.identityprovider.keycloak.KeycloakAdapter;
import com.lulobank.clients.starter.outboundadapter.identityprovider.keycloak.KeycloakConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ImportAutoConfiguration(RefreshAutoConfiguration.class)
@ActiveProfiles(profiles = "test")
public class KeycloakAdapterTest {

  private static final String KEYCLOAK_ID = "keycloak-id";
  private static final String NEW_EMAIL = "new@mail.com";

  private static final String KEYCLOAK_SERVER_URL = "http://localhost:8080/auth";
  private static final String KEYCLOAK_REALM = "keycloak-realm";
  private static final String KEYCLOAK_USER_NAME = "keycloak-user";
  private static final String KEYCLOAK_PASSWORD = "keycloak-password";
  private static final String KEYCLOAK_CLIENT_ID = "keycloak-client-id";

  private static final String USER_REPRESENTATION_ID = "12602b88-a201-4f20-91a0-05bb2f79fb13";
  private static final String USER_REPRESENTATION_USERNAME = "user_representation_username";
  private static final String USER_REPRESENTATION_FIRSTNAME = "user_representation_firstName";
  private static final String USER_REPRESENTATION_EMAIL = "user_representation_email";

  @Mock protected RealmResource realmResourceAdmin;
  @Mock protected UserResource userResource;
  @Mock protected UsersResource usersResource;

  @Captor protected ArgumentCaptor<String> stringArgumentCaptor;
  @Captor protected ArgumentCaptor<UserRepresentation> userRepresentationCaptor;

  private KeycloakAdapter testedClass;
  private UserRepresentation userRepresentation;

  @Before
  public void init() {
    testedClass = new KeycloakAdapter(realmResourceAdmin);

    userRepresentation = new UserRepresentation();
    userRepresentation.setId(USER_REPRESENTATION_ID);
    userRepresentation.setUsername(USER_REPRESENTATION_USERNAME);
    userRepresentation.setFirstName(USER_REPRESENTATION_FIRSTNAME);
    userRepresentation.setEmail(USER_REPRESENTATION_EMAIL);
  }

  @Test
  public void should_create_keycloak_config() {
    KeycloakConfig keycloakConfig = new KeycloakConfig();
    ReflectionTestUtils.setField(keycloakConfig, "serverURL", KEYCLOAK_SERVER_URL);
    ReflectionTestUtils.setField(keycloakConfig, "realm", KEYCLOAK_REALM);
    ReflectionTestUtils.setField(keycloakConfig, "userName", KEYCLOAK_USER_NAME);
    ReflectionTestUtils.setField(keycloakConfig, "password", KEYCLOAK_PASSWORD);
    ReflectionTestUtils.setField(keycloakConfig, "clientId", KEYCLOAK_CLIENT_ID);

    KeycloakAdapter keycloakAdapter = keycloakConfig.getAdapter();
    assertNotNull(keycloakAdapter);
  }

  @Test
  public void should_return_ok_at_changeUserPassword() {
    when(realmResourceAdmin.users()).thenReturn(usersResource);
    when(usersResource.get(any(String.class))).thenReturn(userResource);
    when(userResource.toRepresentation()).thenReturn(userRepresentation);
    doNothing().when(userResource).update(any(UserRepresentation.class));

    testedClass.updateUserEmail(KEYCLOAK_ID, NEW_EMAIL);

    verify(usersResource).get(stringArgumentCaptor.capture());
    verify(userResource).update(userRepresentationCaptor.capture());

    assertEquals(KEYCLOAK_ID, stringArgumentCaptor.getValue());
    assertEquals(NEW_EMAIL, userRepresentationCaptor.getValue().getEmail());
    assertEquals(NEW_EMAIL, userRepresentationCaptor.getValue().getUsername());
  }

}
