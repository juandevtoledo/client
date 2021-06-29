package com.lulobank.clients.starter.config;

import com.lulobank.clients.services.utils.LuloUserTokenGenerator;
import com.lulobank.core.security.spring.WebSecurity;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JWTGenerationSecurityConfig {

  @Value("${spring.security.oauth2.resourceserver.jwt.tenantLulo.public-key-value}")
  private String publicKey;

  @Bean
  public LuloUserTokenGenerator luloUserTokenGenerator()
      throws InvalidKeySpecException, NoSuchAlgorithmException {
    return new LuloUserTokenGenerator(publicKey);
  }

  @Bean
  public WebSecurity webSecurity() {
    return new WebSecurity();
  }
}
