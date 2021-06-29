package com.lulobank.clients.starter.v3.adapters.in.updatecheckpoint.handler;

import com.lulobank.clients.sdk.operations.util.CheckPoints;
import com.lulobank.clients.services.utils.HttpCodes;
import com.lulobank.clients.starter.adapter.in.dto.ErrorResponse;
import com.lulobank.clients.starter.adapter.in.dto.GenericResponse;
import com.lulobank.clients.starter.adapter.in.mapper.NotificationDisabledAdapterMapper;
import com.lulobank.clients.starter.adapter.in.util.AdapterResponseUtil;
import com.lulobank.clients.starter.v3.adapters.in.dto.UpdateCheckpointRequest;
import com.lulobank.clients.starter.v3.adapters.in.dto.UpdateCheckpointResponse;
import com.lulobank.clients.v3.usecase.command.UpdateCheckpointInfo;
import com.lulobank.clients.v3.usecase.updatecheckpoint.UpdateCheckpointUseCase;
import io.vavr.API;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.CustomLog;
import org.springframework.http.ResponseEntity;

import static com.lulobank.clients.starter.adapter.in.mapper.InboundAdapterErrorMapper.getHttpStatusFromBusinessCode;
import static com.lulobank.clients.starter.adapter.in.util.AdapterResponseUtil.error;
import static com.lulobank.clients.v3.error.ClientsDataErrorStatus.CLI_102;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Patterns.$Failure;
import static io.vavr.Patterns.$Success;

@CustomLog
@AllArgsConstructor
public class UpdateCheckpointHandler {

    private final UpdateCheckpointUseCase updateCheckpointUseCase;

    public ResponseEntity<GenericResponse> execute(String clientId, UpdateCheckpointRequest request){
        return API.Match(validateCheckpoint(request)).of(
                Case($Success($()), checkpoint -> getResult(clientId,checkpoint)),
                Case($Failure($()), this::buildBadRequest)
        );
    }

    private Try<CheckPoints> validateCheckpoint(UpdateCheckpointRequest request){
        return Try.of( () -> CheckPoints.valueOf(request.getCheckpoint()));
    }

    private ResponseEntity<GenericResponse> getResult(String clientId,CheckPoints checkpoint){
        return updateCheckpointUseCase.execute(buildCommand(clientId,checkpoint))
                .peekLeft(useCaseResponseError -> log.error(useCaseResponseError.getDetail()))
                .map(this::getResponse)
                .map(AdapterResponseUtil::ok)
                .getOrElseGet(useCaseResponseError -> error(NotificationDisabledAdapterMapper.INSTANCE.toErrorResponse(useCaseResponseError),
                        getHttpStatusFromBusinessCode(useCaseResponseError.getBusinessCode())));
    }

    private ResponseEntity<GenericResponse> buildBadRequest(){
        return AdapterResponseUtil.badRequest(new ErrorResponse(CLI_102.name(), HttpCodes.BAD_REQUEST,CLI_102.getMessage()));
    }

    private UpdateCheckpointInfo buildCommand(String clientId, CheckPoints checkpoint) {
        return UpdateCheckpointInfo.builder()
                .clientId(clientId)
                .checkpoint(checkpoint)
                .build();
    }

    private UpdateCheckpointResponse getResponse(UpdateCheckpointInfo updateCheckpointInfo){
        UpdateCheckpointResponse response = new UpdateCheckpointResponse();
        response.setCheckpoint(updateCheckpointInfo.getCheckpoint());
        return response;
    }
}
