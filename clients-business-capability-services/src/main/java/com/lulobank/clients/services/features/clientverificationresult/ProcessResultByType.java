package com.lulobank.clients.services.features.clientverificationresult;

import com.lulobank.clients.services.application.port.out.reporting.TransactionsMessagingPort;
import com.lulobank.clients.services.events.ClientVerificationResult;
import com.lulobank.clients.services.features.RetriesOption;
import com.lulobank.clients.services.outboundadapters.ClientsOutboundAdapter;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;

import static com.lulobank.clients.services.utils.ProductTypeEnum.valueOf;

/**
 * En esta clase se debe agregar un nuevo case en caso de necesitar un flujo distindo para el onbording
 */
public abstract class ProcessResultByType {

    abstract void process();

    public static ProcessResultByType getProcessByProduct(ClientsOutboundAdapter clientsOutboundAdapter,
                                                          ClientsV3Entity clientEntity,
                                                          RetriesOption retriesOption,
                                                          ClientVerificationResult payload,
                                                          ClientsV3Repository clientsV3Repository,
                                                          TransactionsMessagingPort transactionsMessagingService
    ) {
        ProcessResultByType processResultByType;
        switch (valueOf(clientEntity.getOnBoardingStatus().getProductSelected())) {
            case SAVING_ACCOUNT:
                processResultByType = new ProcessResultBySaving(clientsOutboundAdapter, clientEntity, clientsV3Repository, transactionsMessagingService);
                break;
            default:
                throw new IllegalArgumentException("Client Don't have product");
        }
        return processResultByType;
    }
}
