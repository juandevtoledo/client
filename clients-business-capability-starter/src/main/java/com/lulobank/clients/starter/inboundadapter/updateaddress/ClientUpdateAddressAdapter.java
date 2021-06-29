package com.lulobank.clients.starter.inboundadapter.updateaddress;

import com.amazonaws.SdkClientException;
import com.lulobank.biometric.api.annotation.MFA;
import com.lulobank.clients.sdk.operations.dto.ClientResult;
import com.lulobank.clients.sdk.operations.dto.ClientsFailure;
import com.lulobank.clients.sdk.operations.dto.ClientsFailureResult;
import com.lulobank.clients.sdk.operations.dto.UpdateClientAddressRequest;
import com.lulobank.clients.services.exception.CoreBankingException;
import com.lulobank.clients.services.features.profile.UpdateClientAddressUseCase;
import com.lulobank.clients.services.utils.ClientErrorResultsEnum;
import com.lulobank.clients.services.utils.LogMessages;
import com.lulobank.clients.services.utils.TransactionTypeMFA;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import static com.lulobank.clients.services.utils.ExceptionUtils.getErrorBindingResult;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/")
@Slf4j
public class ClientUpdateAddressAdapter {

    private final UpdateClientAddressUseCase updateClientAddressUseCase;

    public ClientUpdateAddressAdapter(UpdateClientAddressUseCase updateClientAddressUseCase) {
        this.updateClientAddressUseCase = updateClientAddressUseCase;
    }

    @PutMapping(value = "/{idClient}/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    @MFA(transaction = TransactionTypeMFA.UPDATE_ADDRESS,requestBodyClass = UpdateClientAddressRequest.class)
    public ResponseEntity<ClientResult> updateClientInformationV2(
            @RequestHeader final HttpHeaders headers,
            @PathVariable("idClient") @NotBlank(message = "idClient is null or empty") String idClient,
            @Valid @RequestBody final UpdateClientAddressRequest updateClientRequest,
            BindingResult bindingResult) {

        return Option.of(bindingResult)
                .filter(Errors::hasErrors)
                .map(this::getBadRequestResponse)
                .getOrElse(() -> executeUseCase(updateClientRequest,idClient,headers));
        }

    private ResponseEntity<ClientResult> executeUseCase(UpdateClientAddressRequest updateClientRequest, String idClient, HttpHeaders headers) {
        updateClientRequest.setSendNotification(true);
        updateClientRequest.setIdClient(idClient);
        updateClientRequest.setHttpHeaders(headers.toSingleValueMap());
        return updateClientAddressUseCase.execute(updateClientRequest)
                .map(result  -> new ResponseEntity<ClientResult>(HttpStatus.OK))
                .transform(this::handleExceptions)
                .get();
    }

    private Try<ResponseEntity<ClientResult>> handleExceptions(Try<ResponseEntity<ClientResult>> responseEntities) {
        return responseEntities
                .onFailure(SdkClientException.class, e -> log.error(LogMessages.DYNAMO_ERROR_EXCEPTION.getMessage(), e.getMessage(), e))
                .recover(SdkClientException.class, errorResponse(ClientErrorResultsEnum.INTERNAL_SERVER_ERROR.name(), HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .onFailure(CoreBankingException.class, e -> log.error("Error updating client address in core banking. Error {}",
                        e.getMessage(), e))
                .recover(CoreBankingException.class, errorResponse(ClientErrorResultsEnum.CORE_BANKING_ERROR.name(),
                        HttpStatus.PRECONDITION_FAILED.value()))
                .onFailure(Exception.class, e -> log.error(LogMessages.GENERAL_EXCEPTION.getMessage(), e.getMessage(), e))
                .recover(Exception.class, errorResponse(ClientErrorResultsEnum.INTERNAL_SERVER_ERROR.name(),HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    public static ResponseEntity<ClientResult> errorResponse(String message, int code) {
        return ResponseEntity.status(code).body(new ClientsFailureResult<>(new ClientsFailure(message)));
    }

    @NotNull
    private ResponseEntity<ClientResult> getBadRequestResponse(BindingResult bindingResult) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ClientsFailureResult<>(new ClientsFailure(getErrorBindingResult(bindingResult))));
    }
}
