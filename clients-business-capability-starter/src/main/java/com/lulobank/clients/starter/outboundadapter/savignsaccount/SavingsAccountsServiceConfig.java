package com.lulobank.clients.starter.outboundadapter.savignsaccount;

import com.lulobank.clients.services.application.port.out.savingsaccounts.SavingsAccountsPort;
import com.lulobank.clients.starter.adapter.out.savingsaccounts.SavingsAccountsAdapter;
import com.lulobank.clients.starter.adapter.out.savingsaccounts.SavingsAccountsClient;
import com.lulobank.savingsaccounts.sdk.operations.GetSavingsAccountService;
import com.lulobank.savingsaccounts.sdk.operations.ICreateSavingAccountService;
import com.lulobank.savingsaccounts.sdk.operations.ISavingsAccount;
import com.lulobank.savingsaccounts.sdk.operations.impl.CreateSavingAccountService;
import com.lulobank.savingsaccounts.sdk.operations.impl.RetrofitSavingsAccount;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SavingsAccountsServiceConfig {

  @Value("${services.savings.url}")
  private String serviceDomain;

  @Bean
  public GetSavingsAccountService savingsAccountService() {
    return new GetSavingsAccountService(serviceDomain);
  }

  @Bean
  public ICreateSavingAccountService createSavingAccountService() {
    return new CreateSavingAccountService(serviceDomain);
  }

  @Bean
  public ISavingsAccount savingsAccount() {
    return new RetrofitSavingsAccount(serviceDomain);
  }


  @Bean
  public SavingsAccountsClient savingsAccountsClient() {
    return new SavingsAccountsClient(serviceDomain);
  }

  @Bean
  public SavingsAccountsPort getSavingsAccountsPort(SavingsAccountsClient savingsAccountsClient) {
    return new SavingsAccountsAdapter(savingsAccountsClient);
  }
}
