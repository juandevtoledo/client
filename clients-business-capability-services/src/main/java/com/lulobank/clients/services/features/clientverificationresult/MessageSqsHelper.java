package com.lulobank.clients.services.features.clientverificationresult;

import com.lulobank.clients.sdk.operations.dto.economicinformation.ClientEconomicInformation;
import com.lulobank.clients.services.events.ClientVerificationResult;
import com.lulobank.clients.services.events.IdentityInformation;
import com.lulobank.clients.services.features.riskengine.model.ClientCreated;
import com.lulobank.clients.services.outboundadapters.ClientsOutboundAdapter;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.core.Response;

import static java.util.UUID.randomUUID;

public class MessageSqsHelper {

    public static void sendToSqs(ClientVerificationResult payload, ClientsOutboundAdapter clientsOutboundAdapter) {
        clientsOutboundAdapter
                .getMessageToNotifySQSClients()
                .run(new Response(randomUUID().toString()), payload);
    }

    public static void sendToSqs(ClientsV3Entity clientEntity, IdentityInformation identityInformation, ClientsOutboundAdapter clientsOutboundAdapter) {
        clientsOutboundAdapter
                .getMessageToNotifySQSRiskEngine()
                .run(new Response(new ClientCreated(clientEntity.getIdClient())), identityInformation);
    }

    public static void sendToSqs(ClientsOutboundAdapter clientsOutboundAdapter, ClientEconomicInformation clientEconomicInformation) {
        clientsOutboundAdapter.getMessageToNotifySQSEconomicInformation()
                .run(new Response<>(randomUUID()), clientEconomicInformation);
    }

}
