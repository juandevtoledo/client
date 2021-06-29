package com.lulobank.clients.starter.v3.adapters.in.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lulobank.clients.starter.adapter.in.dto.GenericResponse;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApprovedOffersResponse extends GenericResponse {

	private List<ApprovedOffer> offers;

	@Getter
	@Builder
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class ApprovedOffer {
		private String idProductOffer;
		private String state;
		private String type;
		private Integer expiredDays;
		private String description;
		private String additionalInfo;
		private Integer value;
	}
}
