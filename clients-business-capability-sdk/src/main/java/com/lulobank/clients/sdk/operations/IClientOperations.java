package com.lulobank.clients.sdk.operations;

import com.lulobank.clients.sdk.operations.dto.ClientInformationByIdCard;
import com.lulobank.clients.sdk.operations.dto.ClientInformationByIdClient;
import com.lulobank.clients.sdk.operations.dto.ClientInformationByPhone;
import com.lulobank.clients.sdk.operations.dto.ClientInformationByTypeResponse;
import com.lulobank.clients.sdk.operations.dto.DemographicInfoByIdClient;
import com.lulobank.clients.sdk.operations.dto.UpdateClientAddressRequest;
import com.lulobank.clients.sdk.operations.dto.VerifyEmailResponse;
import io.vavr.control.Try;

import java.util.Map;

public interface IClientOperations {

  ClientInformationByIdClient getClientByIdClient(Map<String, String> headers, String idClient);

  ClientInformationByPhone getClientByPhoneNumber(
      Map<String, String> headers, int country, String number);

  ClientInformationByPhone getClientByPhoneNumberInternal(
      Map<String, String> headers, int country, String number);

  VerifyEmailResponse verifyEmailClientInformation(Map<String, String> headers, String email);

  boolean updateClientInformation(
      Map<String, String> headers, UpdateClientAddressRequest updateClientRequest);

  Try<Boolean> updateClientInformationV2(
          Map<String, String> headers, UpdateClientAddressRequest updateClientRequest, String clientId);

  DemographicInfoByIdClient getDemographicInfoByClient(
      Map<String, String> headers, String idClient);

  ClientInformationByIdCard getClientInformationByIdCard(
      Map<String, String> headers, String idCard);

  ClientInformationByIdCard getClientInformationByIdCardInternal(
      Map<String, String> headers, String idCard);

  ClientInformationByTypeResponse getClientByType(
      Map<String, String> headers, String searchType, String value);

  ClientInformationByTypeResponse getClientByTypeInternal(
      Map<String, String> headers, String searchType, String value);
}
