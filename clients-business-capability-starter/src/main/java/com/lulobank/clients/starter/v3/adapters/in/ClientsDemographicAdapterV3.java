package com.lulobank.clients.starter.v3.adapters.in;

import com.lulobank.clients.starter.v3.handler.ClientsDemographicHandler;
import com.lulobank.clients.starter.v3.result.ClientResult;
import com.lulobank.clients.v3.usecase.command.ClientDemographicInfo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v3")
public class ClientsDemographicAdapterV3 {

	private ClientsDemographicHandler clientsDemographicHandler;

	@Autowired
	public ClientsDemographicAdapterV3(ClientsDemographicHandler clientsDemographicHandler) {
		this.clientsDemographicHandler = clientsDemographicHandler;
	}

	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	@ApiResponses(value = {
			@ApiResponse(
					code = 200,
					message = "OK",
					response = ClientDemographicInfo.class)
	})
	@GetMapping(value = "/client/{idClient}/demographic", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ClientResult> getDemographicInfoByClient(@RequestHeader final HttpHeaders headers,
			@Valid @PathVariable("idClient") @NotBlank(message = "IdClient is null or empty") String idClient) {

		return clientsDemographicHandler.getDemographicInfoByClient(idClient);
	}
}
