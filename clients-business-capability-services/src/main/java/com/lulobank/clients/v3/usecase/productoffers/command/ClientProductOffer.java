package com.lulobank.clients.v3.usecase.productoffers.command;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClientProductOffer {
	private final String idProductOffer;
	private final String state;
	private final String type;
	private final Integer expiredDays;
	private final String description;
	private final String additionalInfo;
	private final Integer value;
}
