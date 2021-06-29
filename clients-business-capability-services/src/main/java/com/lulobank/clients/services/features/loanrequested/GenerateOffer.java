package com.lulobank.clients.services.features.loanrequested;

import com.amazonaws.SdkClientException;
import com.lulobank.clients.sdk.operations.dto.ClientLoanRequested;
import com.lulobank.clients.services.outboundadapters.ClientsOutboundAdapter;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.utils.ProductTypeEnum;
import com.lulobank.credits.sdk.exceptions.InitialOffersException;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.owasp.encoder.Encode;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import static com.lulobank.clients.services.utils.ClientHelper.getHeadersClientCredentials;
import static com.lulobank.clients.services.utils.LogMessages.DYNAMO_ERROR_EXCEPTION;
import static com.lulobank.clients.services.utils.LogMessages.NOT_LOAN_AMOUNT_TO_CLIENT;
import static com.lulobank.clients.services.utils.ProductTypeEnum.CREDIT_ACCOUNT;


@Slf4j
public abstract class GenerateOffer {
    public static final String ERROR_INITIALS_OFFER =
            "Error while generating offers in LoanRequested Service message: {}, serviceCode: {} ,serviceMessage: {}, clientId: {}";
    private ClientsOutboundAdapter clientsOutboundAdapter;

    public GenerateOffer(ClientsOutboundAdapter clientsOutboundAdapter) {
        this.clientsOutboundAdapter = clientsOutboundAdapter;
    }

    public void generate(ClientEntity clientEntity, ClientLoanRequested clientLoanRequested) {
        if (validateUpdateOffers(clientEntity)) {
            Try.run(
                    () ->
                            consumerInitialsOffer(clientLoanRequested)
                                    .andThen(saveClient)
                                    .andThen(notify)
                                    .accept(clientEntity))
                    .onFailure(InitialOffersException.class, handlerInitialOfferException(clientEntity))
                    .onFailure(SdkClientException.class, handlerSdkException(clientEntity));
        }
    }

    private Consumer<ClientEntity> consumerInitialsOffer(ClientLoanRequested clientLoanRequested) {
        return clientEntity ->
                clientsOutboundAdapter
                        .getInitialOffersOperations()
                        .initialOffers(
                                getHeadersClientCredentials(clientsOutboundAdapter, clientEntity.getIdClient()),
                                InitialOffersMapper.INSTANCE.getOfferClientFrom(clientEntity,clientLoanRequested),
                                clientEntity.getIdClient());
    }


    private Consumer<ClientEntity> saveClient =
            clientEntity -> clientsOutboundAdapter.getClientsRepository().save(clientEntity);

    private Consumer<ClientEntity> notify = this::notifyLoanFinished;


    protected static GenerateOffer getGenerateOffer(
            ClientEntity clientEntity, ClientsOutboundAdapter clientsOutboundAdapter) {
        GenerateOffer generateOffer = null;
        if (isFromHome(clientEntity)) {
            generateOffer = new GenerateOfferFromHome(clientsOutboundAdapter);
        } else if (isFromOnboarding(clientEntity)) {
            generateOffer = new GenerateOfferFromOnboarding(clientsOutboundAdapter);
        }
        return Optional.ofNullable(generateOffer)
                .orElseThrow(
                        () ->
                                new UnsupportedOperationException(
                                        NOT_LOAN_AMOUNT_TO_CLIENT.getMessage().concat(clientEntity.getIdClient())));
    }

    private Consumer<InitialOffersException> handlerInitialOfferException(ClientEntity clientEntity) {
        return ex -> {
            log.error(
                    ERROR_INITIALS_OFFER,
                    ex.getMessage(),
                    ex.getServiceCode(),
                    ex.getServiceMessage(),
                    Encode.forJava(clientEntity.getIdClient()));
            notifyLoanFailed(clientEntity);
        };
    }

    private Consumer<SdkClientException> handlerSdkException(ClientEntity clientEntity) {
        return ex -> {
            log.error(
                    DYNAMO_ERROR_EXCEPTION.getMessage(), Encode.forJava(clientEntity.getIdClient()), ex.getMessage(), ex);
            notifyLoanFailed(clientEntity);
        };
    }

    private static boolean isFromHome(ClientEntity clientEntity) {
        return Objects.nonNull(clientEntity.getLoanRequested());
    }

    private static boolean isFromOnboarding(ClientEntity clientEntity) {
        return CREDIT_ACCOUNT.equals(
                ProductTypeEnum.valueOf(clientEntity.getOnBoardingStatus().getProductSelected()));
    }

    abstract void notifyLoanFinished(ClientEntity clientEntity);

    abstract void notifyLoanFailed(ClientEntity clientEntity);

    abstract Map<String, Object> getFirebaseMessages(ClientEntity clientEntity);

    abstract boolean validateUpdateOffers(ClientEntity clientEntity);
}
