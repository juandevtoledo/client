package com.lulobank.clients.services.application.usecase.zendeskclientinfo;

import com.lulobank.clients.services.application.port.in.ZendeskClientInfoPort;
import com.lulobank.clients.services.application.port.out.clientsdata.ClientsDataRepositoryPort;
import com.lulobank.clients.services.application.port.out.debitcards.DebitCardsPort;
import com.lulobank.clients.services.application.port.out.savingsaccounts.SavingsAccountsPort;
import com.lulobank.clients.services.application.port.out.savingsaccounts.model.SavingAccount;
import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.services.domain.zendeskclientinfo.GetClientInfoByEmailRequest;
import com.lulobank.clients.services.domain.zendeskclientinfo.GetClientInfoByEmailResponse;
import com.lulobank.clients.services.domain.zendeskclientinfo.mapper.ZendeskClientInfoMapper;
import com.lulobank.clients.services.utils.AccountStatusEnum;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Predicate;

@Slf4j
@RequiredArgsConstructor
public class ZendeskClientInfoUseCase implements ZendeskClientInfoPort {

    private final ClientsDataRepositoryPort clientsDataRepositoryPort;
    private final SavingsAccountsPort savingsAccountsPort;
    private final DebitCardsPort debitCardsPort;

    @Override
    public Either<UseCaseResponseError, GetClientInfoByEmailResponse> execute(GetClientInfoByEmailRequest command) {
        return clientsDataRepositoryPort.findByEmailAddress(command.getEmail())
                .mapLeft(clientsDataError -> (UseCaseResponseError) clientsDataError)
                .flatMap(clientsV3Entity -> getSavingsAccountClient(command, clientsV3Entity))
                .flatMap(clientInfo -> getDebitCardsInfoClient(command, clientInfo));
    }

    private Either<UseCaseResponseError, GetClientInfoByEmailResponse> getSavingsAccountClient(GetClientInfoByEmailRequest command, ClientsV3Entity client) {
        GetClientInfoByEmailResponse clientInfo =
                ZendeskClientInfoMapper.INSTANCE.toGetClientInfoByEmailUseCaseResponse(client);
        return savingsAccountsPort.getSavingsAccountsByIdClient(command.getAuthorizationHeader(), clientInfo.getIdClient())
                .mapLeft(savingsAccountsError -> (UseCaseResponseError) savingsAccountsError)
                .peek(accountByIdClient -> Option.of(accountByIdClient)
                        .filter(isActiveOrApprovedAccount)
                        .peek(account -> clientInfo.getProducts().add(ZendeskClientInfoMapper.INSTANCE.toProduct(account))))
                .map(accountByIdClient -> clientInfo);

    }

    private Either<UseCaseResponseError, GetClientInfoByEmailResponse> getDebitCardsInfoClient(GetClientInfoByEmailRequest command, GetClientInfoByEmailResponse clientInfo) {
        return debitCardsPort.getDebitCardByIdClient(command.getAuthorizationHeader(), clientInfo.getIdClient())
                .mapLeft(savingsAccountsError -> (UseCaseResponseError) savingsAccountsError)
                .peek(debitCard -> debitCardsPort.getDebitCardStatusByIdClient(command.getAuthorizationHeader(),clientInfo.getIdClient())
                        .mapLeft(debitCardsError -> (UseCaseResponseError) debitCardsError)
                        .peek(cardStatus -> Option.of(cardStatus)
                            .filter(status -> status.getStatus().equals("ACTIVE"))
                            .peek(status -> clientInfo.getProducts().add(ZendeskClientInfoMapper.INSTANCE.toProduct(debitCard,
                                    status)))))
                .map(accountByIdClient -> clientInfo);

    }

    private final Predicate<SavingAccount> isActiveOrApprovedAccount =
            account ->
                    AccountStatusEnum.ACTIVE.name().equals(account.getState())
                            || AccountStatusEnum.APPROVED.name().equals(account.getState());
}
