package com.lulobank.clients.starter;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lulobank.clients.services.domain.StateBlackList;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@RunWith(SpringRunner.class)
@ImportAutoConfiguration(RefreshAutoConfiguration.class)
@ActiveProfiles(profiles = "test")
public class ClientAdapterTest extends AbstractBaseIntegrationTest {

  private static final String URL_GET_CLIENT_BY_PHONE_INTERNAL = "/internalPhonenumber";

  private static final String TESTED_URL = "/login";
  private static final String PASSWORD = "123456";
  private static final String NEW_EMAIL = "newmail@mail.com";
  private static final String ID_CARD = "12345678";
  private static final String NAME = "usertest";
  private static final String LAST_NAME = "lastname_test";
  private static final int PHONE_PREFIX = 57;
  private static final String PHONE_NUMBER_1 = "3168906733";
  private static final String ADDRESS = "address_test";
  private ClientEntity clientEntity;

  @Override
  protected void init() {
    clientEntity = new ClientEntity();
    clientEntity.setIdClient(ID_CLIENT);
    clientEntity.setIdCard(ID_CARD);
    clientEntity.setIdCard(PASSWORD);
    clientEntity.setName(NAME);
    clientEntity.setLastName(LAST_NAME);
    clientEntity.setAddress(ADDRESS);
    clientEntity.setPhonePrefix(PHONE_PREFIX);
    clientEntity.setPhoneNumber(PHONE_NUMBER_1);
    clientEntity.setEmailAddress(NEW_EMAIL);
    clientEntity.setEmailVerified(Boolean.TRUE);
    clientEntity.setIdCard(ID_CARD);
    clientEntity.setBlackListState(StateBlackList.NON_BLACKLISTED.name());
  }

  @Test
  public void shouldOkAndReturnClientByPhoneNumber() throws Exception {
    when(clientsRepository.findByPhonePrefixAndPhoneNumber(any(Integer.class), anyString()))
        .thenReturn(Optional.of(clientEntity));
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(URL_GET_CLIENT_BY_PHONE_INTERNAL)
                .param("country", String.valueOf(PHONE_PREFIX))
                .param("number", PHONE_NUMBER_1)
                .with(bearerTokenAWS())
                .contentType(CONTENT_TYPE_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("content.idClient", is(ID_CLIENT)));
  }

  @Test
  public void should_Not_Return_Transaction_Client_And_Status_NotFound() throws Exception {
    when(clientsRepository.findByPhonePrefixAndPhoneNumber(any(Integer.class), anyString()))
        .thenReturn(Optional.empty());
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(URL_GET_CLIENT_BY_PHONE_INTERNAL)
                .param("country", String.valueOf(PHONE_PREFIX))
                .param("number", PHONE_NUMBER_1)
                .with(bearerTokenAWS())
                .contentType(CONTENT_TYPE_JSON))
        .andExpect(status().isNotFound());
  }
}
