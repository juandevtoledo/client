package com.lulobank.clients.starter.v3.adapters.in.createaddress.handler;

import com.lulobank.clients.starter.adapter.in.dto.GenericResponse;
import com.lulobank.clients.starter.adapter.in.mapper.NotificationDisabledAdapterMapper;
import com.lulobank.clients.starter.adapter.in.util.AdapterResponseUtil;
import com.lulobank.clients.starter.v3.adapters.in.dto.CreateAddressRequest;
import com.lulobank.clients.starter.v3.adapters.in.dto.CreateAddressResponse;
import com.lulobank.clients.v3.usecase.command.ClientAddressData;
import com.lulobank.clients.v3.usecase.createaddress.ClientCreateAddressUseCase;
import lombok.AllArgsConstructor;
import lombok.CustomLog;
import org.springframework.http.ResponseEntity;

import static com.lulobank.clients.starter.adapter.in.mapper.InboundAdapterErrorMapper.getHttpStatusFromBusinessCode;
import static com.lulobank.clients.starter.adapter.in.util.AdapterResponseUtil.error;

@CustomLog
@AllArgsConstructor
public class ClientCreateAddressHandler {

    private final ClientCreateAddressUseCase clientCreateAddressUseCase;

    public ResponseEntity<GenericResponse> execute(String idClient, CreateAddressRequest request){
        return clientCreateAddressUseCase.execute(buildCommand(idClient,request))
                .peekLeft(useCaseResponseError -> log.error(useCaseResponseError.getDetail()))
                .map(this::getResult)
                .map( response -> AdapterResponseUtil.created(response))
                .getOrElseGet(useCaseResponseError -> error(NotificationDisabledAdapterMapper.INSTANCE.toErrorResponse(useCaseResponseError),
                        getHttpStatusFromBusinessCode(useCaseResponseError.getBusinessCode())));
    }

    private ClientAddressData buildCommand(String idClient,CreateAddressRequest request){
        return ClientAddressData.builder()
                .idClient(idClient)
                .address(request.getAddress())
                .addressPrefix(request.getAddressPrefix())
                .addressComplement(request.getAddressComplement())
                .city(request.getCity())
                .cityId(request.getCityId())
                .department(request.getDepartment())
                .departmentId(request.getDepartmentId())
                .code(request.getCode())
                .build();
    }

    private CreateAddressResponse getResult(ClientAddressData clientAddressData){
        CreateAddressResponse createAddressRequest =  new CreateAddressResponse();
        createAddressRequest.setAddress(clientAddressData.getAddress());
        createAddressRequest.setAddressPrefix(clientAddressData.getAddressPrefix());
        createAddressRequest.setAddressComplement(clientAddressData.getAddressComplement());
        createAddressRequest.setCity(clientAddressData.getCity());
        createAddressRequest.setCityId(clientAddressData.getCityId());
        createAddressRequest.setDepartment(clientAddressData.getDepartment());
        createAddressRequest.setDepartmentId(clientAddressData.getDepartmentId());
        createAddressRequest.setCode(clientAddressData.getCode());
        return createAddressRequest;
    }

}
