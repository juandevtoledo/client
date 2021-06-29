package com.lulobank.clients.services.features.clientproducts.model;

import com.lulobank.core.Command;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductsBasicInfo implements Command {
  private boolean allSavingsAccountsCloseable;
  private List<SavingsAccount> savingsAccounts;
  private List<String> availableClosingMethods;
  private List<Credit> credits;
}
