package com.lulobank.clients.starter.outboundadapter.credits;

import com.lulobank.credits.sdk.operations.IClientProductOfferOperations;
import com.lulobank.credits.sdk.operations.InitialOffersOperations;
import com.lulobank.credits.sdk.operations.impl.ClientProductOfferOperations;
import com.lulobank.credits.sdk.operations.impl.RetrofitGetLoanDetailOperations;
import com.lulobank.credits.sdk.operations.impl.RetrofitInitialOffersOperations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CreditsServiceConfig {
  @Value("${services.credits.url}")
  private String serviceDomain;

  @Bean
  public RetrofitGetLoanDetailOperations getLoanDetailOperationsService() {
    return new RetrofitGetLoanDetailOperations(serviceDomain);
  }

  @Bean
  public IClientProductOfferOperations acceptOffer() {
    return new ClientProductOfferOperations(serviceDomain);
  }

  @Bean
  public InitialOffersOperations getinitialOffers() {
    return new RetrofitInitialOffersOperations(serviceDomain);
  }
}
