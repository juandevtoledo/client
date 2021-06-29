package com.lulobank.clients.services.ports.out.corebanking;

import com.lulobank.clients.sdk.operations.dto.UpdateClientAddressRequest;

public interface ClientInfoCoreBankingPort {

    void updateAddressCoreBanking(UpdateClientAddressRequest updateClientAddressRequest, String idCbs);

}
