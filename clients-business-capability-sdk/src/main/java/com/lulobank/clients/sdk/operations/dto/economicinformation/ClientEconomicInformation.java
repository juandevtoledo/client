package com.lulobank.clients.sdk.operations.dto.economicinformation;

import com.lulobank.clients.sdk.operations.AdapterCredentials;
import com.lulobank.clients.sdk.operations.dto.AbstractCommandFeatures;
import com.lulobank.core.Command;
import com.lulobank.core.utils.IgnoreValidation;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ClientEconomicInformation extends AbstractCommandFeatures implements Command {

  private String idClient;
  private OccupationType occupationType;
  @Valid
  private EmployeeCompany employeeCompany = new EmployeeCompany();
  @Size(max = 6)
  @Pattern(regexp = "^$|[0-9]+")
  private String economicActivity;
  @PositiveOrZero
  private BigDecimal monthlyIncome;
  @PositiveOrZero
  private BigDecimal monthlyOutcome;
  @PositiveOrZero
  private BigDecimal additionalIncome;
  @PositiveOrZero
  private BigDecimal assets;
  @PositiveOrZero
  private BigDecimal liabilities;
  @IgnoreValidation private List<ForeignCurrencyTransaction> foreignCurrencyTransactions;
  @Size(max = 100)
  private String savingPurpose = "";
  @Size(max = 100)
  private String typeSaving = "";
  @IgnoreValidation
  private AdapterCredentials adapterCredentials;
}