package com.lulobank.clients.starter.v3.result;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClientFailureResult implements ClientResult {

	private String code;
	private String failure;
	private String detail;

	public ClientFailureResult(ClientErrorCode errorCode, String failure) {
		this.code = errorCode.getErrorCode();
		this.failure = failure;
	}

	public ClientFailureResult(ClientErrorCode errorCode, String failure, String detail) {
		this.code = errorCode.getErrorCode();
		this.failure = failure;
		this.detail = detail;
	}
}
