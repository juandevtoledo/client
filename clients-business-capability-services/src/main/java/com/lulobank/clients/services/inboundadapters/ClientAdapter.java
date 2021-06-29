package com.lulobank.clients.services.inboundadapters;

import com.lulobank.clients.sdk.operations.dto.ClientErrorResult;
import com.lulobank.clients.sdk.operations.dto.ClientInformationByIdCard;
import com.lulobank.clients.sdk.operations.dto.ClientInformationByTypeResponse;
import com.lulobank.clients.sdk.operations.dto.ClientResult;
import com.lulobank.clients.sdk.operations.dto.ClientSuccessResult;
import com.lulobank.clients.sdk.operations.dto.ClientsFailure;
import com.lulobank.clients.sdk.operations.dto.ClientsFailureResult;
import com.lulobank.clients.sdk.operations.dto.DemographicInfoByIdClient;
import com.lulobank.clients.sdk.operations.dto.GetClientInformationByIdCard;
import com.lulobank.clients.sdk.operations.dto.VerifyEmailResponse;
import com.lulobank.clients.sdk.operations.dto.onboardingclients.ClientInformationByIdClient;
import com.lulobank.clients.sdk.operations.dto.onboardingclients.GetClientInformationByIdClient;
import com.lulobank.clients.services.ILoginAttempts;
import com.lulobank.clients.services.actions.CreateCognitoClientAction;
import com.lulobank.clients.services.actions.MessageToSQSRiskEngine;
import com.lulobank.clients.services.application.port.out.transactions.savingsaccounts.TransactionsPort;
import com.lulobank.clients.services.exception.ValidateRequestException;
import com.lulobank.clients.services.features.clientproducts.ProductsBasicInfoHandler;
import com.lulobank.clients.services.features.clientproducts.closuremethodvalidator.ClosureMethodValidator;
import com.lulobank.clients.services.features.clientproducts.model.Client;
import com.lulobank.clients.services.features.clientproducts.model.ProductsBasicInfo;
import com.lulobank.clients.services.features.clientproducts.validators.ClientValidator;
import com.lulobank.clients.services.features.login.ClientLoginHandler;
import com.lulobank.clients.services.features.login.model.SignUp;
import com.lulobank.clients.services.features.login.model.SignUpResult;
import com.lulobank.clients.services.features.onboardingclients.ClientHandler;
import com.lulobank.clients.services.features.onboardingclients.GetClientInformationByIdClientHandler;
import com.lulobank.clients.services.features.onboardingclients.GetClientInformationByPhoneHandler;
import com.lulobank.clients.services.features.onboardingclients.GetClientInformationByTypeHandler;
import com.lulobank.clients.services.features.onboardingclients.GetDemographicInfoByIdClientHandler;
import com.lulobank.clients.services.features.onboardingclients.model.ClientInformationByPhone;
import com.lulobank.clients.services.features.onboardingclients.model.ClientInformationByTypeRequest;
import com.lulobank.clients.services.features.onboardingclients.model.CreateClientRequest;
import com.lulobank.clients.services.features.onboardingclients.model.CreateClientResponse;
import com.lulobank.clients.services.features.onboardingclients.model.GetClientInformationByPhone;
import com.lulobank.clients.services.features.onboardingclients.model.GetDemographicInfoByIdClient;
import com.lulobank.clients.services.features.onboardingclients.validators.CreateClientRequestValidateEmail;
import com.lulobank.clients.services.features.onboardingclients.validators.CreateClientRequestValidateIdCard;
import com.lulobank.clients.services.features.onboardingclients.validators.CreateClientRequestValidateIdClient;
import com.lulobank.clients.services.features.onboardingclients.validators.CreateClientRequestValidatePhone;
import com.lulobank.clients.services.features.productsloanrequested.ProductsLoanRequestedWithClient;
import com.lulobank.clients.services.features.profile.UpdateClientEmailHandler;
import com.lulobank.clients.services.features.profile.VerifyEmailHandler;
import com.lulobank.clients.services.features.profile.action.MessageToSQSCustomerEmailAddressUpdated;
import com.lulobank.clients.services.features.profile.action.MessageToSQSNotificationEmailUpdated;
import com.lulobank.clients.services.features.profile.model.UpdateEmailClientRequest;
import com.lulobank.clients.services.features.profile.model.VerifyEmailRequest;
import com.lulobank.clients.services.features.profile.validators.UpdateClientEmailValidator;
import com.lulobank.clients.services.features.riskengine.ClientWithIdCardInformationHandler;
import com.lulobank.clients.services.features.riskengine.model.ClientCreated;
import com.lulobank.clients.services.features.riskengine.model.ClientWithIdCardInformation;
import com.lulobank.clients.services.features.riskengine.validators.ClientWithIdCardInformationValidator;
import com.lulobank.clients.services.outboundadapters.SendMessageToSQS;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.clients.services.utils.ClientErrorResultsEnum;
import com.lulobank.clients.services.utils.CognitoProperties;
import com.lulobank.clients.services.utils.LoginErrorResultsEnum;
import com.lulobank.core.Response;
import com.lulobank.core.actions.Action;
import com.lulobank.core.crosscuttingconcerns.PostActionsDecoratorHandler;
import com.lulobank.core.crosscuttingconcerns.ValidatorDecoratorHandler;
import com.lulobank.core.utils.ValidatorUtils;
import com.lulobank.core.validations.ValidationResult;
import com.lulobank.core.validations.Validator;
import com.lulobank.credits.sdk.operations.impl.RetrofitGetLoanDetailOperations;
import flexibility.client.sdk.FlexibilitySdk;
import io.vavr.control.Try;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.lulobank.core.utils.ValidatorUtils.getHttpStatusByCode;
import static org.springframework.http.HttpStatus.ACCEPTED;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*")
public class ClientAdapter {
    private ClientsRepository repository;
    private QueueMessagingTemplate queueMessagingTemplate;
    private CognitoProperties cognitoProperties;
    private FlexibilitySdk flexibilitySdk;
    private RetrofitGetLoanDetailOperations retrofitGetLoanDetailOperations;
    private ILoginAttempts loginAttemptsService;
    private Map<String, ClosureMethodValidator> closureMethodValidatorMap;
    private TransactionsPort transactionsPort;

  @Value("${cloud.aws.sqs.riskengine-events}")
  private String sqsRiskEngineEndPoint;

  @Value("${cloud.aws.sqs.client-alerts-events}")
  private String sqsNotificationEndPoint;

  @Value("${cloud.aws.sqs.customer-events}")
  private String sqsCustomerEndPoint;

  @Autowired
  @Qualifier("productsLoanRequestedHandler")
  private ValidatorDecoratorHandler productsLoanRequestedHandler;

  @Autowired
  @Qualifier("getClientInfoByIdCardHandler")
  private ValidatorDecoratorHandler getClientInfoByIdCardHandler;


    @Autowired
    public ClientAdapter(
            ClientsRepository repository,
            QueueMessagingTemplate queueMessagingTemplate,
            CognitoProperties cognitoProperties,
            FlexibilitySdk flexibilitySdk,
            RetrofitGetLoanDetailOperations retrofitGetLoanDetailOperations,
            ILoginAttempts loginAttemptsService,
            ValidatorDecoratorHandler getClientInfoByIdCardHandler,
            Map<String, ClosureMethodValidator> closureMethodValidatorMap,
            TransactionsPort transactionsPort) {
        this.repository = repository;
        this.queueMessagingTemplate = queueMessagingTemplate;
        this.cognitoProperties = cognitoProperties;
        this.flexibilitySdk = flexibilitySdk;
        this.retrofitGetLoanDetailOperations = retrofitGetLoanDetailOperations;
        this.loginAttemptsService = loginAttemptsService;
        this.getClientInfoByIdCardHandler = getClientInfoByIdCardHandler;
        this.closureMethodValidatorMap = closureMethodValidatorMap;
        this.transactionsPort = transactionsPort;
    }

  @PostMapping()
  public ResponseEntity<Response<CreateClientResponse>> saveClient(
      @RequestHeader final HttpHeaders headers,
      @RequestBody final CreateClientRequest createClientRequest) {
    List<Validator<CreateClientRequest>> validators = new ArrayList<>();
    validators.add(new CreateClientRequestValidateIdClient());
    validators.add(new CreateClientRequestValidateIdCard(repository));
    validators.add(new CreateClientRequestValidateEmail(repository));
    validators.add(new CreateClientRequestValidatePhone(repository));
    List<Action<Response<CreateClientResponse>, CreateClientRequest>> actions = new ArrayList<>();
    actions.add(new CreateCognitoClientAction(cognitoProperties, repository));
    Response<CreateClientResponse> response =
        new ValidatorDecoratorHandler<>(
                new PostActionsDecoratorHandler<>(new ClientHandler(repository), actions),
                validators)
            .handle(createClientRequest);
    if (Boolean.FALSE.equals(response.getHasErrors()))
      return ResponseEntity.accepted().body(response);
    if (response.getErrors().stream()
        .anyMatch(x -> ClientErrorResultsEnum.CLIENT_NOT_FOUND_IN_DB.name().equals(x.getFailure())))
      return new ResponseEntity<>(HttpStatus.CONFLICT);
    return ResponseEntity.badRequest().body(response);
  }

  @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Response<SignUpResult>> login(
      @RequestHeader final HttpHeaders headers, @RequestBody final SignUp signUp) {

    Response<SignUpResult> response =
        new ClientLoginHandler(cognitoProperties, repository, loginAttemptsService).handle(signUp);
    if (Boolean.FALSE.equals(response.getHasErrors())) {
      return new ResponseEntity<>(new Response<>(response.getContent()), HttpStatus.OK);
    }
    if (response.getErrors().stream()
        .anyMatch(
            x -> LoginErrorResultsEnum.COGNITO_NOT_AUTHORIZED.name().equals(x.getFailure()))) {
      return new ResponseEntity<>(new Response<>(response.getErrors()), HttpStatus.UNAUTHORIZED);
    }
    if (response.getErrors().stream()
        .anyMatch(x -> LoginErrorResultsEnum.COGNITO_NOT_FOUND.name().equals(x.getFailure()))) {
      return new ResponseEntity<>(new Response<>(response.getErrors()), HttpStatus.UNAUTHORIZED);
    }
    if (response.getErrors().stream()
        .anyMatch(x -> LoginErrorResultsEnum.USER_BLOCKED.name().equals(x.getFailure()))) {
      return new ResponseEntity<>(new Response<>(response.getErrors()), HttpStatus.FORBIDDEN);
    }
    if (response.getErrors().stream()
        .anyMatch(x -> LoginErrorResultsEnum.SERVICE_ERROR.name().equals(x.getFailure()))) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  }

  @PostMapping(value = "/id-client")
  public ResponseEntity<Response<ClientCreated>> initialSaveClient(
      @RequestHeader final HttpHeaders headers,
      @RequestBody final ClientWithIdCardInformation clientWithIdCardInformation) {
    List<Validator<ClientWithIdCardInformation>> validators = new ArrayList<>();
    validators.add(new ClientWithIdCardInformationValidator());
    List<Action<Response<ClientCreated>, ClientWithIdCardInformation>> actions = new ArrayList<>();
    actions.add(
        (SendMessageToSQS)
            new MessageToSQSRiskEngine(queueMessagingTemplate, sqsRiskEngineEndPoint));
    Response<ClientCreated> response =
        new ValidatorDecoratorHandler<>(
                new PostActionsDecoratorHandler<>(
                    new ClientWithIdCardInformationHandler(repository), actions),
                validators)
            .handle(clientWithIdCardInformation);
    if (Boolean.FALSE.equals(response.getHasErrors()))
      return ResponseEntity.accepted().body(response);
    if (response.getErrors().stream()
        .anyMatch(x -> ClientErrorResultsEnum.CLIENT_CREATED.name().equals(x.getFailure())))
      return new ResponseEntity<>(HttpStatus.FOUND);
    return ResponseEntity.badRequest().body(response);
  }

  @PostMapping(value = "/validclient")
  public ResponseEntity<Response<ClientCreated>> validateClient(
      @RequestHeader final HttpHeaders headers,
      @RequestBody final ClientWithIdCardInformation clientWithIdCardInformation) {
    List<Validator<ClientWithIdCardInformation>> validators = new ArrayList<>();
    validators.add(new ClientWithIdCardInformationValidator());
    List<Action<Response<ClientCreated>, ClientWithIdCardInformation>> actions = new ArrayList<>();
    Response<ClientCreated> response =
        new ValidatorDecoratorHandler<>(
                new PostActionsDecoratorHandler<>(
                    new ClientWithIdCardInformationHandler(repository), actions),
                validators)
            .handle(clientWithIdCardInformation);
    if (Boolean.FALSE.equals(response.getHasErrors())) {
      return ResponseEntity.accepted().body(response);
    }
    if (response.getErrors().stream()
        .anyMatch(x -> ClientErrorResultsEnum.CLIENT_CREATED.name().equals(x.getFailure()))) {
      return new ResponseEntity<>(HttpStatus.FOUND);
    }
    return ResponseEntity.badRequest().body(response);
  }

  @GetMapping(value = "/phonenumber", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Response<ClientInformationByPhone>> getClientByPhoneNumber(
      @RequestHeader final HttpHeaders headers,
      @RequestParam("country") final int country,
      @RequestParam("number") final String number) {
    return getClientByPhoneNumber(country, number);
  }

  @GetMapping(value = "/internalPhonenumber", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Response<ClientInformationByPhone>> getClientByPhoneNumberInternal(
      @RequestHeader final HttpHeaders headers,
      @RequestParam("country") final int country,
      @RequestParam("number") final String number) {
    return getClientByPhoneNumber(country, number);
  }

  @NotNull
  private ResponseEntity<Response<ClientInformationByPhone>> getClientByPhoneNumber(
      @RequestParam("country") int country, @RequestParam("number") String number) {
    Response<ClientInformationByPhone> response =
        new GetClientInformationByPhoneHandler(repository)
            .handle(new GetClientInformationByPhone(country, number));
    if (Boolean.FALSE.equals(response.getHasErrors())) return ResponseEntity.ok().body(response);
    if (response.getErrors().stream()
        .anyMatch(x -> ClientErrorResultsEnum.CLIENT_NOT_FOUND_IN_DB.name().equals(x.getFailure())))
      return ResponseEntity.notFound().build();
    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  }

  @GetMapping(value = "/idClient/{idClient}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Response<ClientInformationByIdClient>> getClientByIdClient(
      @RequestHeader final HttpHeaders headers, @PathVariable("idClient") final String idClient) {
    Response<ClientInformationByIdClient> response =
        new GetClientInformationByIdClientHandler(repository, flexibilitySdk)
            .handle(new GetClientInformationByIdClient(idClient));

    if (Boolean.FALSE.equals(response.getHasErrors())) return ResponseEntity.ok().body(response);
    if (response.getErrors().stream()
        .anyMatch(x -> ClientErrorResultsEnum.CLIENT_NOT_FOUND_IN_DB.name().equals(x.getFailure())))
      return ResponseEntity.notFound().build();
    if (response.getErrors().stream()
        .anyMatch(
            x ->
                ClientErrorResultsEnum.CLIENT_DB_ERROR.name().equals(x.getFailure())
                    || ClientErrorResultsEnum.SDK_FLEXIBILITY_ERROR
                        .name()
                        .equals(x.getFailure()))) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  }

  @GetMapping(value = "/idCard", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ClientResult> getClientByIdCard(
      @RequestHeader final HttpHeaders headers, @RequestParam("idCard") final String idCard) {
    return getClientResultIdCardResponseEntity(idCard);
  }

  @GetMapping(value = "/idCardInternal", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ClientResult> getClientByIdCardInternal(
      @RequestHeader final HttpHeaders headers, @RequestParam("idCard") final String idCard) {
    return getClientResultIdCardResponseEntity(idCard);
  }

  private ResponseEntity<ClientResult> getClientResultIdCardResponseEntity(
      @RequestParam("idCard") String idCard) {
    Response<ClientInformationByIdCard> response =
        getClientInfoByIdCardHandler.handle(new GetClientInformationByIdCard(idCard));

    if (Boolean.TRUE.equals(response.getHasErrors())) {
      return new ResponseEntity<>(
          new ClientErrorResult(response.getErrors()),
          getHttpStatusByCode(response.getErrors().get(0).getValue()));
    }
    return new ResponseEntity<>(new ClientSuccessResult<>(response.getContent()), HttpStatus.OK);
  }

  @PutMapping(value = "/{idClient}/profile/email", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ClientResult> updateClientEmail(
      @RequestHeader final HttpHeaders headers,
      @PathVariable String idClient,
      @RequestBody final UpdateEmailClientRequest updateEmailClientRequest) {
    updateEmailClientRequest.setIdClient(idClient);
    updateEmailClientRequest.setHttpHeaders(headers.toSingleValueMap());

    UpdateClientEmailValidator updateEmailClientValidator = new UpdateClientEmailValidator(repository);
    SendMessageToSQS<UpdateEmailClientRequest> sqsNotificationEmailUpdateSender =
        new MessageToSQSNotificationEmailUpdated(queueMessagingTemplate, sqsNotificationEndPoint);
    SendMessageToSQS<UpdateEmailClientRequest> sqsCustomerEmailAddressUpdateSender =
        new MessageToSQSCustomerEmailAddressUpdated(queueMessagingTemplate, sqsCustomerEndPoint);
    UpdateClientEmailHandler updateEmailHandler = new UpdateClientEmailHandler(repository, cognitoProperties, sqsNotificationEmailUpdateSender, sqsCustomerEmailAddressUpdateSender);

    return Try.of(() -> updateEmailClientRequest)
        .map(updateEmailClientValidator::validate)
        .map(updateEmailHandler::handle)
        .recover(ValidateRequestException.class, vre -> ResponseEntity
            .status(vre.getCode())
            .body(new ClientsFailureResult<>(new ClientsFailure(vre.getFailure()))))
        .get();
  }

  @GetMapping(value = "/profile/email/verify/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Response<VerifyEmailResponse>> verifyEmailClientInformation(
      @RequestHeader final HttpHeaders headers, @PathVariable("email") final String email) {
    Response<VerifyEmailResponse> response =
        new VerifyEmailHandler(repository).handle(new VerifyEmailRequest(email));

    if (Boolean.FALSE.equals(response.getHasErrors())) {
      return ResponseEntity.ok().body(response);
    }
    if (response.getErrors().stream()
        .anyMatch(
            x -> ClientErrorResultsEnum.CLIENT_NOT_FOUND_IN_DB.name().equals(x.getFailure()))) {
      return ResponseEntity.notFound().build();
    }
    if (response.getErrors().stream()
        .anyMatch(
            x -> ClientErrorResultsEnum.INTERNAL_SERVER_ERROR.name().equals(x.getFailure()))) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  }

    @GetMapping(value = "/products/{idClient}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClientResult> getProductsBasicInfoByClient(
            @RequestHeader final HttpHeaders headers, @PathVariable("idClient") final String idClient) {
        Client client = new Client();
        client.setIdClient(idClient);
        client.setHttpHeaders(headers.toSingleValueMap());
        List<Validator<Client>> validators = new ArrayList<>();
        validators.add(new ClientValidator(repository));
        Response<ProductsBasicInfo> response =
                new ValidatorDecoratorHandler<>(
                        new ProductsBasicInfoHandler(
                                flexibilitySdk,
                                retrofitGetLoanDetailOperations,
                                repository,
                                closureMethodValidatorMap,
                                transactionsPort),
                        validators)
                        .handle(client);
        if (Boolean.FALSE.equals(response.getHasErrors())) {
            return new ResponseEntity<>(new ClientSuccessResult<>(response.getContent()), HttpStatus.OK);
        }
        return new ResponseEntity<>(
                new ClientErrorResult(response.getErrors()),
                getHttpStatusByCode(response.getErrors().get(0).getValue()));
    }

    @PostMapping(
            value = "/{idClient}/products/request/loan",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClientResult> productsLoanRequested(
            @RequestHeader final HttpHeaders headers, @PathVariable final String idClient) {

        Response<ClientResult> response =
                productsLoanRequestedHandler.handle(new ProductsLoanRequestedWithClient(idClient, headers));
        if (Boolean.TRUE.equals(response.getHasErrors())) {
            return new ResponseEntity<>(
                    new ClientErrorResult(response.getErrors()),
                    ValidatorUtils.getHttpStatusByCode(response.getErrors().get(0).getValue()));
        }
        return ResponseEntity.status(ACCEPTED.value()).build();
    }

  /**
   * @deprecated (see ClientsDemographicAdapterV3
   * new url /api/v3/client/{idClient}/demographic)
   */
  @Deprecated
  @GetMapping(value = "/demographic/{idClient}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ClientResult> getDemographicInfoByClient(
      @RequestHeader final HttpHeaders headers, @PathVariable final String idClient) {

    Response<DemographicInfoByIdClient> response =
        new GetDemographicInfoByIdClientHandler(repository)
            .handle(new GetDemographicInfoByIdClient(idClient));

    if (response.getHasErrors()) {
      return new ResponseEntity<>(
          new ClientErrorResult(response.getErrors()),
          getHttpStatusByCode(response.getErrors().get(0).getValue()));
    }
    return new ResponseEntity<>(new ClientSuccessResult<>(response.getContent()), HttpStatus.OK);
  }

  @GetMapping(value = "/searchType")
  public ResponseEntity<ClientResult> getClientByType(
      @RequestHeader final HttpHeaders headers,
      @RequestParam("searchType") final String searchType,
      @RequestParam("value") final String value) {
    Response<ClientInformationByTypeResponse> response =
        new GetClientInformationByTypeHandler(repository)
            .handle(new ClientInformationByTypeRequest(searchType, value));
    if (response.getHasErrors()) {
      Optional<ValidationResult> validationResult = response.getErrors().stream().findFirst();
      return validationResult
          .map(getClientByTypeError(response, validationResult))
          .orElseGet(getClientErrorResult(response));
    }
    return new ResponseEntity<>(new ClientSuccessResult<>(response.getContent()), HttpStatus.OK);
  }

  @GetMapping(value = "/searchTypeInternal")
  public ResponseEntity<ClientResult> getClientByTypeInternal(
      @RequestHeader final HttpHeaders headers,
      @RequestParam("searchType") final String searchType,
      @RequestParam("value") final String value) {
    Response<ClientInformationByTypeResponse> response =
        new GetClientInformationByTypeHandler(repository)
            .handle(new ClientInformationByTypeRequest(searchType, value));
    if (response.getHasErrors()) {
      Optional<ValidationResult> validationResult = response.getErrors().stream().findFirst();
      return validationResult
          .map(getClientByTypeError(response, validationResult))
          .orElseGet(getClientErrorResult(response));
    }
    return new ResponseEntity<>(new ClientSuccessResult<>(response.getContent()), HttpStatus.OK);
  }

  private Function<ValidationResult, ResponseEntity<ClientResult>> getClientByTypeError(
      Response response, Optional<ValidationResult> validationResult) {
    return validation ->
        new ResponseEntity<ClientResult>(
            new ClientErrorResult(response.getErrors()),
            getHttpStatusByCode(validationResult.get().getValue()));
  }

  private Supplier<ResponseEntity<ClientResult>> getClientErrorResult(Response response) {
    return () ->
        new ResponseEntity<>(
            new ClientErrorResult(response.getErrors()),
            getHttpStatusByCode(HttpStatus.INTERNAL_SERVER_ERROR.name()));
  }
}
