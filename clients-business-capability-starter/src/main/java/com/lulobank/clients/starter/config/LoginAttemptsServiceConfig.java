package com.lulobank.clients.starter.config;

import com.lulobank.clients.services.ILoginAttempts;
import com.lulobank.clients.services.outboundadapters.repository.LoginAttemptsRepository;
import com.lulobank.clients.services.utils.LoginAttemptsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoginAttemptsServiceConfig {

  @Bean
  public ILoginAttempts loginAttemptsService(
      LoginAttemptsRepository loginAttemptsRepository, AttemptsConfig attemptsConfig) {
    return new LoginAttemptsService(loginAttemptsRepository, attemptsConfig.getAttempts());
  }
}
