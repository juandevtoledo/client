package com.lulobank.clients.services.actions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.lulobank.clients.services.features.changepassword.actions.MessageToSQSNotificationPasswordUpdated;
import com.lulobank.clients.services.features.changepassword.model.NewPasswordRequest;
import com.lulobank.clients.services.features.changepassword.model.Password;
import com.lulobank.core.Response;
import com.lulobank.core.events.Event;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

public class MessageToSQSNotificationPasswordUpdatedTest {
  private static final String ENDPOINT = "http://sqs.dev.local:9324/queue/default";
  @Mock QueueMessagingTemplate queueMessagingTemplate;
  private MessageToSQSNotificationPasswordUpdated testedClass;
  private Response response;
  private NewPasswordRequest newPasswordRequest;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    this.testedClass =
        new MessageToSQSNotificationPasswordUpdated(queueMessagingTemplate, ENDPOINT);
    Password body = new Password("12345");
    response = new Response(body);
    newPasswordRequest = new NewPasswordRequest();
    newPasswordRequest.setAccessToken("28nx9823nx98wqex9qne");
    newPasswordRequest.setEmailAddress("mail@mail.com");
    newPasswordRequest.setIdClient("1106bc49-4a0f-4f52-86ca-1994bb3c26d9");
    newPasswordRequest.setNewPassword("123456");
    newPasswordRequest.setOldPassword("123457");

    Map<String, String> httpHeaders = new HashMap<>();
    httpHeaders.put("authenticationToken", "fakeToken");

    newPasswordRequest.setHttpHeaders(httpHeaders);
  }

  @Test
  public void validate_Send_SQSMessage_To_Black_List_Queue() {
    testedClass.run(response, newPasswordRequest);
    verify(queueMessagingTemplate, times(1))
        .convertAndSend(anyString(), any(Event.class), anyMap());
  }
}
