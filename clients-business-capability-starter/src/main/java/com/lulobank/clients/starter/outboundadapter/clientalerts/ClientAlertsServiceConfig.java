package com.lulobank.clients.starter.outboundadapter.clientalerts;

import com.lulobank.clientalerts.sdk.operations.impl.RetrofitClientNotificationsOperations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientAlertsServiceConfig {
  @Value("${services.client-alerts.url}")
  private String serviceDomain;

  @Bean
  public RetrofitClientNotificationsOperations getRetrofitClientNotificationsOperations() {
    return new RetrofitClientNotificationsOperations(serviceDomain);
  }
}
