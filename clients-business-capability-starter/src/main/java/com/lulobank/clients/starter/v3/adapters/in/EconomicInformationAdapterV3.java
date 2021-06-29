package com.lulobank.clients.starter.v3.adapters.in;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lulobank.clients.sdk.operations.dto.economicinformation.ClientEconomicInformation;
import com.lulobank.clients.starter.v3.handler.EconomicInformationHandler;
import com.lulobank.clients.starter.v3.result.ClientResult;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v3")
public class EconomicInformationAdapterV3 {
	
	private final EconomicInformationHandler economicInformationHandler;
	
	public EconomicInformationAdapterV3(EconomicInformationHandler economicInformationHandler) {
		this.economicInformationHandler = economicInformationHandler;
	}
	
	@PostMapping(value = "/client/{idClient}/economic-information")
	public ResponseEntity<ClientResult> saveEconomicInformation(@RequestHeader final HttpHeaders headers,
			@Valid @PathVariable("idClient") @NotBlank(message = "IdClient is null or empty") String idClient,
			@Valid @RequestBody ClientEconomicInformation clientEconomicInformation) {
		clientEconomicInformation.setIdClient(idClient);
		return economicInformationHandler.saveEconomicInformation(clientEconomicInformation,headers.toSingleValueMap());
	}

}
