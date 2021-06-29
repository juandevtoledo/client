package com.lulobank.clients.starter.outboundadapter.otp;

import com.lulobank.otp.sdk.operations.impl.RetrofitOtpOperations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OtpServiceConfig {
  @Value("${services.otp.url}")
  private String serviceDomain;

  @Bean
  public RetrofitOtpOperations getRetrofitOtpOperations() {
    return new RetrofitOtpOperations(serviceDomain);
  }
}
