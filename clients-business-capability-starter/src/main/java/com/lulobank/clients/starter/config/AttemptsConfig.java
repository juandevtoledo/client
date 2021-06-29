package com.lulobank.clients.starter.config;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "login")
public class AttemptsConfig {
  private Map<Integer, String> attempts;

  public Map<Integer, String> getAttempts() {
    return attempts;
  }

  public void setAttempts(Map<Integer, String> attempts) {
    this.attempts = attempts;
  }
}
