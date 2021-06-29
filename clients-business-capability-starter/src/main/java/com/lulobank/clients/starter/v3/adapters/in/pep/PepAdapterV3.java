package com.lulobank.clients.starter.v3.adapters.in.pep;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lulobank.clients.starter.v3.handler.pep.PepHandler;
import com.lulobank.clients.v3.usecase.command.UpdatePepRequest;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v3")
public class PepAdapterV3 {

	private PepHandler pepHandler;

	@Autowired
	public PepAdapterV3(PepHandler updatePepHandler) {
		this.pepHandler = updatePepHandler;
	}

	@PostMapping(value = "/client/{idClient}/pep")
	public ResponseEntity<Object> updatePepClient(@RequestHeader final HttpHeaders headers,
			@Valid @PathVariable("idClient") @NotBlank(message = "IdClient is null or empty") String idClient,
			@RequestBody UpdatePepRequest updatePepRequest) {
		updatePepRequest.setIdClient(idClient);
		return pepHandler.updatePep(updatePepRequest);
	}
	
	@GetMapping(value = "/client/{idClient}/pep")
	public ResponseEntity<Object> getPepClient(@RequestHeader final HttpHeaders headers,
			@Valid @PathVariable("idClient") @NotBlank(message = "IdClient is null or empty") String idClient) {
		return pepHandler.getPep(idClient);
	}
}
