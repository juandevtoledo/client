package com.lulobank.clients.starter.v3.adapters.config;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ConfigurationProperties("banner.product-offer")
public class ProductOfferTypeConfig {
	private Map<String, List<String>> validators;
	private Map<String, Integer> expiredDays;
	private Map<String, String> descriptions;
	private Map<String, String> additionalInfo;
}
