package com.lulobank.clients.services.features.profile;

import com.lulobank.clients.sdk.operations.dto.UpdateClientAddressRequest;
import com.lulobank.clients.services.exception.ClientNotFoundException;
import com.lulobank.clients.services.features.profile.mapper.ClientsEntityV3Mapper;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.ports.out.MessageService;
import com.lulobank.clients.services.ports.out.corebanking.ClientInfoCoreBankingPort;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import io.vavr.concurrent.Future;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UpdateClientAddressService {

    private final ClientsV3Repository clientsRepository;
    private final MessageService messageService;
    private final ClientInfoCoreBankingPort clientInfoCoreBankingPort;

    public UpdateClientAddressService(ClientsV3Repository clientsRepository,
                                      MessageService messageService,
                                      ClientInfoCoreBankingPort clientInfoCoreBankingPort) {
        this.clientsRepository = clientsRepository;
        this.messageService = messageService;
        this.clientInfoCoreBankingPort = clientInfoCoreBankingPort;
    }


    public Try<Boolean> updateAddress(UpdateClientAddressRequest command) {
        return clientsRepository.findByIdClient(command.getIdClient())
                .toTry(ClientNotFoundException::new)
                .peek(client -> updateAddress(client, command))
                .map(client -> true);

    }

    private void updateAddress(ClientsV3Entity clientEntity, UpdateClientAddressRequest command) {
        Option.of(clientEntity)
                .filter(client -> isClientAddressChange(command, client))
                .peek(client -> clientInfoCoreBankingPort.updateAddressCoreBanking(command, client.getIdCbs()))
                .peek(clientsRepository::save)
                .map(clientsV3Entity -> ClientsEntityV3Mapper.INSTANCE.toClientEntity(clientEntity))
                .peek(client -> sendNotification(client, command.isSendNotification()));
    }

    private void sendNotification(ClientEntity clientEntity, boolean sendNotification) {
        if (sendNotification) Future.run(() -> messageService.sendNotificationUpdateAddress(clientEntity));
    }

    private Boolean isClientAddressChange(UpdateClientAddressRequest updateClientProfileRequest, ClientsV3Entity clientEntity) {

        Boolean addressChanged = false;
        if (isAddressChanged(updateClientProfileRequest, clientEntity)) {
            clientEntity.setAddress(updateClientProfileRequest.getAddress());
            clientEntity.setAddressPrefix(updateClientProfileRequest.getAddressPrefix());
            clientEntity.setCode(updateClientProfileRequest.getCode());
            clientEntity.setAddressComplement(updateClientProfileRequest.getAddressComplement());
            addressChanged = true;
        }
        if (isCityChanged(updateClientProfileRequest, clientEntity)) {
            clientEntity.setCity(updateClientProfileRequest.getCity());
            clientEntity.setCityId(updateClientProfileRequest.getCityId());
            addressChanged = true;
        }
        if (isDepartmentChanged(updateClientProfileRequest, clientEntity)) {
            clientEntity.setDepartment(updateClientProfileRequest.getDepartment());
            clientEntity.setDepartmentId(updateClientProfileRequest.getDepartmentId());
            addressChanged = true;
        }
        return addressChanged;

    }

    private Boolean isDepartmentChanged(UpdateClientAddressRequest updateClientProfileRequest,
                                        ClientsV3Entity clientEntity) {

        return !updateClientProfileRequest.getDepartment().equals(clientEntity.getDepartment());
    }

    private Boolean isCityChanged(UpdateClientAddressRequest updateClientProfileRequest,
                                  ClientsV3Entity clientEntity) {

        return !updateClientProfileRequest.getCity().equals(clientEntity.getCity());
    }

    private Boolean isAddressChanged(UpdateClientAddressRequest updateClientProfileRequest,
                                     ClientsV3Entity clientEntity) {

        return !updateClientProfileRequest.getAddress().equals(clientEntity.getAddress())
                || !updateClientProfileRequest.getAddressPrefix().equals(clientEntity.getAddressPrefix())
                || !updateClientProfileRequest.getAddressComplement().equals(clientEntity.getAddressComplement())
                || !updateClientProfileRequest.getCode().equals(clientEntity.getCode());
    }
}
