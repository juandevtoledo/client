package com.lulobank.clients.starter.v3.result;

public enum ClientErrorCode {
	
	CLIENT_UNEXPECTED_ERROR("100"),
	CLIENT_DEMOGRAPHIC_NOT_FOUND_ERROR("001"),
	CLIENT_UPDATE_BIOMETRIC_ERROR("002"),
	CLIENT_UPDATE_PEP_ERROR("003"),
	CLIENT_RETRIEVED_PEP_ERROR("004"),
	CLIENT_SAVE_ECONOMIC_INFORMATION_ERROR("103");

	private static final String ERROR_PREFIX = "CLI_";
	
	private final String errorCode;
	
	ClientErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	
	public String getErrorCode() {
		return ERROR_PREFIX.concat(errorCode);
	}
}
