package com.lulobank.clients.sdk.operations;

import java.math.BigInteger;
import java.util.Map;
import org.springframework.http.ResponseEntity;

public interface IClientValidationOperations {

  ResponseEntity<String> validateEmail(Map<String, String> headers, String email);

  ResponseEntity<String> validatePhone(Map<String, String> headers, int country, BigInteger phone);

  ResponseEntity<String> validateIdCard(Map<String, String> headers, String idCard);
}
