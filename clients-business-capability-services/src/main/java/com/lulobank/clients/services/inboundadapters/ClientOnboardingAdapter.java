package com.lulobank.clients.services.inboundadapters;

import com.lulobank.clients.sdk.operations.dto.ClientLoanRequested;
import com.lulobank.clients.sdk.operations.dto.economicinformation.ClientEconomicInformation;
import com.lulobank.clients.sdk.operations.dto.onboarding.changeproduct.ChangeOnBoardingSelectedProductToSavingsForClient;
import com.lulobank.clients.sdk.operations.dto.resetidentitybiometric.ClientToReset;
import com.lulobank.clients.services.features.identitybiometric.model.UpdateIdTransactionBiometric;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.core.Response;
import com.lulobank.core.crosscuttingconcerns.ValidatorDecoratorHandler;
import com.lulobank.core.validations.ValidationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.lulobank.clients.services.utils.ExceptionUtils.getResponseEntityError;
import static com.lulobank.core.utils.ValidatorUtils.getHttpStatusByCode;

@RefreshScope
@RestController
@RequestMapping("/onboarding")
@CrossOrigin(origins = "*")
public class ClientOnboardingAdapter {
  @Autowired
  @Qualifier("loanRequestedDecoratorHandler")
  private ValidatorDecoratorHandler loanRequestedDecoratorHandler;

  @Autowired
  @Qualifier("economicInformationDecoratorHandler")
  private ValidatorDecoratorHandler economicInformationDecoratorHandler;

  @Autowired
  @Qualifier("changeOnBoardingSelectedProductToSavingsForClientHandler")
  private ValidatorDecoratorHandler changeOnBoardingSelectedProductToSavingsForClientHandler;

  @Autowired
  @Qualifier("updateIdTransactionBiometricHandler")
  private ValidatorDecoratorHandler updateIdTransactionBiometricHandler;

  @Autowired
  @Qualifier("resetBiometricIdentityHandler")
  private ValidatorDecoratorHandler resetBiometricIdentityHandler;

  @Autowired
  public ClientOnboardingAdapter(
      ValidatorDecoratorHandler loanRequestedDecoratorHandler,
      ValidatorDecoratorHandler economicInformationDecoratorHandler,
      ValidatorDecoratorHandler changeOnBoardingSelectedProductToSavingsForClientHandler,
      ValidatorDecoratorHandler updateIdTransactionBiometricHandler,
      ValidatorDecoratorHandler resetBiometricIdentityHandler) {
    this.loanRequestedDecoratorHandler = loanRequestedDecoratorHandler;
    this.economicInformationDecoratorHandler = economicInformationDecoratorHandler;
    this.changeOnBoardingSelectedProductToSavingsForClientHandler =
        changeOnBoardingSelectedProductToSavingsForClientHandler;
    this.updateIdTransactionBiometricHandler = updateIdTransactionBiometricHandler;
    this.resetBiometricIdentityHandler = resetBiometricIdentityHandler;
  }

  /** @deprecated as security issues were identified with this endpoint */
  @Deprecated
  @PostMapping(value = "/requested/loan")
  public ResponseEntity<Response<ClientEntity>> saveLoanRequested(
      @RequestHeader final HttpHeaders headers, @RequestBody final ClientLoanRequested request) {
    Response response = loanRequestedDecoratorHandler.handle(request);
    if (Boolean.TRUE.equals(response.getHasErrors())) {
      return new ResponseEntity<>(
          new Response(response.getErrors()),
          getHttpStatusByCode(((ValidationResult) response.getErrors().get(0)).getValue()));
    }
    return ResponseEntity.status(HttpStatus.CREATED.value()).build();
  }

  @PostMapping(value = "/{idClient}/requested/loan")
  public ResponseEntity<Response<ClientEntity>> saveRequestedLoan(
      @RequestHeader final HttpHeaders headers,
      @PathVariable("idClient") String idClient,
      @RequestBody final ClientLoanRequested request) {
    request.setIdClient(idClient);
    Response<ClientEntity> response = loanRequestedDecoratorHandler.handle(request);
    if (Boolean.TRUE.equals(response.getHasErrors())) return getResponseEntityError(response);
    return ResponseEntity.status(HttpStatus.CREATED.value()).build();
  }

  /** @deprecated as security issues were identified with this endpoint */
  @Deprecated
  @PostMapping(value = "/economicinformation")
  public ResponseEntity<Response<ClientEntity>> saveEconomicInformation(
      @RequestHeader final HttpHeaders headers,
      @RequestBody final ClientEconomicInformation request) {
    Response<ClientEntity> response = economicInformationDecoratorHandler.handle(request);
    if (Boolean.TRUE.equals(response.getHasErrors())) {
      return new ResponseEntity<>(
          new Response(response.getErrors()),
          getHttpStatusByCode(((ValidationResult) response.getErrors().get(0)).getValue()));
    }
    return ResponseEntity.status(HttpStatus.CREATED.value()).build();
  }

  @PostMapping(value = "/{idClient}/economicinformation")
  public ResponseEntity<Response> saveEconomicInformation(
      @RequestHeader final HttpHeaders headers,
      @PathVariable("idClient") String idClient,
      @RequestBody final ClientEconomicInformation request) {
    request.setIdClient(idClient);
    request.setHttpHeaders(headers.toSingleValueMap());
    Response response = economicInformationDecoratorHandler.handle(request);
    if (Boolean.TRUE.equals(response.getHasErrors())) return getResponseEntityError(response);
    return ResponseEntity.status(HttpStatus.CREATED.value()).build();
  }

  @PostMapping(
      value = "/{idClient}/change/product/savings",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Response> changeProductSelectOnBoarding(
      @RequestHeader final HttpHeaders headers, @PathVariable final String idClient) {
    ChangeOnBoardingSelectedProductToSavingsForClient
        changeOnBoardingSelectedProductToSavingsForClient =
            new ChangeOnBoardingSelectedProductToSavingsForClient(idClient);
    changeOnBoardingSelectedProductToSavingsForClient.setHttpHeaders(headers.toSingleValueMap());
    Response response =
        changeOnBoardingSelectedProductToSavingsForClientHandler.handle(
            changeOnBoardingSelectedProductToSavingsForClient);
    if (Boolean.TRUE.equals(response.getHasErrors())) {
      return new ResponseEntity<>(
          new Response(response.getErrors()),
          getHttpStatusByCode(((ValidationResult) response.getErrors().get(0)).getValue()));
    }
    return ResponseEntity.status(HttpStatus.OK.value()).build();
  }

  @PostMapping(value = "/identity/biometric")
  public ResponseEntity<Response> updateIdentityBiometric(
      @RequestHeader final HttpHeaders headers,
      @RequestBody final UpdateIdTransactionBiometric request) {

    Response response = updateIdTransactionBiometricHandler.handle(request);
    if (Boolean.TRUE.equals(response.getHasErrors())) {
      return new ResponseEntity<>(
          new Response(response.getErrors()),
          getHttpStatusByCode(((ValidationResult) response.getErrors().get(0)).getValue()));
    }
    return ResponseEntity.status(HttpStatus.ACCEPTED.value()).build();
  }

  @PostMapping(value = "/identity/biometric/reset")
  public ResponseEntity<Response> resetBiometricIdentity(
      @RequestHeader final HttpHeaders headers, @RequestBody final ClientToReset request) {

    Response response = resetBiometricIdentityHandler.handle(request);
    if (Boolean.TRUE.equals(response.getHasErrors())) {
      return new ResponseEntity<>(
          new Response(response.getErrors()),
          getHttpStatusByCode(((ValidationResult) response.getErrors().get(0)).getValue()));
    }
    return ResponseEntity.status(HttpStatus.ACCEPTED.value()).build();
  }

}
