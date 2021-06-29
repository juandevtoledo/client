package com.lulobank.clients.starter.inboundadapter;

import com.lulobank.clients.services.exception.ClientsNotificationException;
import com.lulobank.clients.services.exception.DigitalEvidenceException;
import com.lulobank.clients.services.exception.IdentityProviderException;
import com.lulobank.clients.services.exception.InitialClientTokenException;
import com.lulobank.clients.services.exception.TimestampDigitalEvidenceException;
import com.lulobank.clients.services.exception.ValidateRequestException;
import com.lulobank.clients.services.features.initialclient.model.InitialClientCreated;
import com.lulobank.clients.services.inboundadapters.model.ClientsFailureResult;
import com.lulobank.clients.services.inboundadapters.model.ClientsResult;
import com.lulobank.clients.services.inboundadapters.model.ClientsSuccessResult;
import com.lulobank.clients.services.usecase.InitialClientUseCase;
import com.lulobank.clients.services.utils.ClientsErrorResponse;
import com.lulobank.clients.starter.adapter.in.dto.ErrorResponse;
import com.lulobank.clients.starter.inboundadapter.dto.CreateInitialClientRequest;
import com.lulobank.clients.starter.inboundadapter.mapper.InboundsMapper;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.lulobank.clients.services.utils.ClientsErrorResponse.CLIENT_NOTIFICATION_ERROR;
import static com.lulobank.clients.services.utils.ClientsErrorResponse.IDENTITY_PROVIDER_ERROR;
import static com.lulobank.clients.services.utils.ClientsErrorResponse.JWT_GENERATION_ERROR;
import static com.lulobank.clients.services.utils.ClientsErrorResponse.DIGITAL_EVIDENCE_ERROR;
import static com.lulobank.clients.services.utils.ClientsErrorResponse.TIMESTAMP_ERROR;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/V2/onboarding")
@Slf4j
public class ClientOnboardingAdapterV2 {

    private static final String ERROR_GENERATING_JWT = "Problems generating token for new client. Cause: {}.";
    private static final String ERROR_NOTIFYING_CREATION = "Problems notifying creation of new client. Cause: {}.";
    private static final String ERROR_IDENTITY_PROVIDER = "Problems creating user in Keycloak. Cause: {}.";
    private static final String ERROR_SAVING_DIGITAL_EVIDENCE = "Error saving digital evidence. Cause: {}";
    private static final String ERROR_TIMESTAMP = "Error processing documents acceptance timestamp. Cause: {}";
    private static final String ERROR_UNKNOWN = "An error was produced at the creation of the initial client. Cause: {}";

    private static final String DIGITAL_EVIDENCE_FAILURE = "500";

    private final InitialClientUseCase initialClientUseCase;

    @Autowired
    public ClientOnboardingAdapterV2(InitialClientUseCase initialClientUseCase) {
        this.initialClientUseCase = initialClientUseCase;
    }

    @PostMapping(value = "/initialClient", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Client Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "client_token"),
            @ApiImplicitParam(name = "firebase-id", value = "Firebase Id", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "firebaseId"),
    })
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "Client was created",
                    response = InitialClientCreated.class),
            @ApiResponse(
                    code = 404,
                    message = "Not found",
                    response = ErrorResponse.class),
            @ApiResponse(
                    code = 401,
                    message = "Unauthorized",
                    response = ErrorResponse.class),
    })
    public ResponseEntity<ClientsResult> createInitialClient(
            @RequestHeader final HttpHeaders headers,
            @Valid @RequestHeader("firebase-id") final String firebaseId,
            @Valid @RequestBody final CreateInitialClientRequest createInitialClient) {

        return Try.success(createInitialClient)
                .map(InboundsMapper.INSTANCE::toCommand)
                .andThenTry(command -> command.setHttpHeaders(headers.toSingleValueMap()))
                .flatMap(initialClientUseCase::execute)
                .map(ClientsSuccessResult::new)
                .map(result -> new ResponseEntity<ClientsResult>(result, HttpStatus.CREATED))
                .transform(this::handleInitialClientErrors)
                .get();
    }

    private Try<ResponseEntity<ClientsResult>> handleInitialClientErrors(Try<ResponseEntity<ClientsResult>> responseEntities) {
        return responseEntities
                .recover(ValidateRequestException.class, this::handleValidationException)
                .recover(IdentityProviderException.class, e -> handleError(e, IDENTITY_PROVIDER_ERROR, ERROR_IDENTITY_PROVIDER))
                .recover(ClientsNotificationException.class, e -> handleError(e, CLIENT_NOTIFICATION_ERROR, ERROR_NOTIFYING_CREATION))
                .recover(InitialClientTokenException.class, e -> handleError(e, JWT_GENERATION_ERROR, ERROR_GENERATING_JWT))
                .recover(DigitalEvidenceException.class, this::digitalEvidenceError)
                .recover(TimestampDigitalEvidenceException.class, e -> handleError(e, TIMESTAMP_ERROR, ERROR_TIMESTAMP))
                .onFailure(e -> log.error(ERROR_UNKNOWN, e.getMessage(), e))
                .recover(e -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ClientsFailureResult()));
    }

    private ResponseEntity<ClientsResult> digitalEvidenceError(DigitalEvidenceException dee) {
        log.error(ERROR_SAVING_DIGITAL_EVIDENCE, dee.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ClientsFailureResult()
                        .setCode(DIGITAL_EVIDENCE_ERROR.code())
                        .setFailure(DIGITAL_EVIDENCE_FAILURE)
                        .setDetail("U")
                );
    }

    private ResponseEntity<ClientsResult> handleValidationException(ValidateRequestException e) {
        return Option.of(e)
                .map(ValidateRequestException::getFailure)
                .map(failureDetails -> new ClientsFailureResult()
                        .setCode(null)
                        .setFailure(HttpStatus.BAD_REQUEST.name())
                        .setDetail(failureDetails)
                )
                .map(result -> new ResponseEntity<ClientsResult>(result, HttpStatus.BAD_REQUEST))
                .get();
    }

    private ResponseEntity<ClientsResult> handleError(Throwable e, ClientsErrorResponse failure, String logMessage) {
        log.error(logMessage, e.getMessage(), e);
        ClientsResult failureResult = new ClientsFailureResult()
                .setCode(failure.code())
                .setFailure(failure.name())
                .setDetail(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(failureResult);
    }

}
