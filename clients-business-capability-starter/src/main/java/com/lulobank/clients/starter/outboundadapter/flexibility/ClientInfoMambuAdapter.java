package com.lulobank.clients.starter.outboundadapter.flexibility;

import com.lulobank.clients.sdk.operations.dto.UpdateClientAddressRequest;
import com.lulobank.clients.services.exception.CoreBankingException;
import com.lulobank.clients.services.ports.out.corebanking.ClientInfoCoreBankingPort;
import com.lulobank.clients.starter.outboundadapter.flexibility.mapper.ClientInfoMapper;
import flexibility.client.connector.ProviderException;
import flexibility.client.models.request.UpdateClientRequest;
import flexibility.client.sdk.FlexibilitySdk;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

@Slf4j
@RequiredArgsConstructor
public class ClientInfoMambuAdapter implements ClientInfoCoreBankingPort {

    private final FlexibilitySdk flexibilitySdk;

    @Override
    public void updateAddressCoreBanking(UpdateClientAddressRequest updateClientAddressRequest, String idCbs) {
        Try.of(() -> flexibilitySdk.getClientById(ClientInfoMapper.INSTANCE.toGetClientRequest(idCbs)))
                .map(ClientInfoMapper.INSTANCE::toUpdateClientRequest)
                .peek(updateClientRequest -> updateClientRequest.setAddress(buildUpdateClientAddress(updateClientAddressRequest)))
                .andThenTry(flexibilitySdk::updateClient)
                .onFailure(ProviderException.class, e -> {
                    throw new CoreBankingException("Error updating address in core banking.", e);
                });
    }

    private UpdateClientRequest.Address buildUpdateClientAddress(UpdateClientAddressRequest updateClientAddressRequest) {
        UpdateClientRequest.Address address = ClientInfoMapper.INSTANCE.toUpdateClientRequestAddress(updateClientAddressRequest);
        String fullAddress = buildFullAddress(updateClientAddressRequest);
        address.setAddress(fullAddress);
        address.setDescription(fullAddress);
        return address;
    }

    private String buildFullAddress(UpdateClientAddressRequest updateClientAddressRequest) {
        return updateClientAddressRequest.getAddressPrefix()
                .concat(" ")
                .concat(updateClientAddressRequest.getAddress())
                .concat(Option.of(updateClientAddressRequest.getAddressComplement())
                        .map(" "::concat)
                        .getOrElse(Strings.EMPTY)
                );
    }

}
