package com.lulobank.clients.v3.service.productoffers;

import java.util.Map;

import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;

public interface ProductOfferValidatorService {
	
	boolean validateProductOffer(ClientsV3Entity clientsV3Entity, Map<String,String> auth);
	
}
