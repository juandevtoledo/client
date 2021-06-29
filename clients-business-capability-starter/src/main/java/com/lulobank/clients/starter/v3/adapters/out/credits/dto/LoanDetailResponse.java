package com.lulobank.clients.starter.v3.adapters.out.credits.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanDetailResponse {

	private String idCredit;
	private String idLoanCBS;
	private Money requestedAmount;
	private Money payOffAmount;
	private Money paidAmount;
	private String state;
	private Integer paidInstallments;
	private Integer installments;
	private LocalDateTime createOn;
	private LocalDateTime closedDate;
	private Rates rates;

	@Getter
	@Setter
	public static class Money {
		private BigDecimal value;
		private String currency;
	}

	@Getter
	@Setter
	public static class Rates {
		private BigDecimal monthlyNominal;
		private BigDecimal annualEffective;
	}

}
