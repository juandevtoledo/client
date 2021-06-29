package com.lulobank.clients.services.features.clientproducts;

import com.lulobank.clients.services.application.port.out.transactions.savingsaccounts.TransactionsPort;
import com.lulobank.clients.services.exception.ClientNotFoundException;
import com.lulobank.clients.services.features.clientproducts.closuremethodvalidator.ClosureMethodValidator;
import com.lulobank.clients.services.features.clientproducts.model.Client;
import com.lulobank.clients.services.features.clientproducts.model.Credit;
import com.lulobank.clients.services.features.clientproducts.model.ProductsBasicInfo;
import com.lulobank.clients.services.features.clientproducts.model.SavingsAccount;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.clients.services.utils.BalanceClosureMethods;
import com.lulobank.clients.services.utils.ClientErrorResultsEnum;
import com.lulobank.clients.services.utils.DatesUtil;
import com.lulobank.clients.services.utils.MambuErrorsResultEnum;
import com.lulobank.clients.services.utils.MoneyUtil;
import com.lulobank.clients.services.utils.TransactionClosureMethods;
import com.lulobank.clients.services.utils.UtilValidators;
import com.lulobank.core.Handler;
import com.lulobank.core.Response;
import com.lulobank.credits.sdk.dto.loandetails.LoanDetail;
import com.lulobank.credits.sdk.dto.loandetails.LoanDetailRetrofitResponse;
import com.lulobank.credits.sdk.operations.impl.RetrofitGetLoanDetailOperations;
import com.lulobank.utils.exception.ServiceException;
import flexibility.client.connector.ProviderException;
import flexibility.client.models.request.GetAccountRequest;
import flexibility.client.models.request.GetLoanRequest;
import flexibility.client.models.response.GetAccountResponse;
import flexibility.client.models.response.GetLoanResponse;
import flexibility.client.sdk.FlexibilitySdk;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.lulobank.core.utils.ValidatorUtils.getListValidations;

@RequiredArgsConstructor
public class ProductsBasicInfoHandler implements Handler<Response<ProductsBasicInfo>, Client> {

    private final FlexibilitySdk flexibilitySdk;
    private final RetrofitGetLoanDetailOperations getLoanDetailOperationsService;
    private final ClientsRepository clientsRepository;
    private final Map<String, ClosureMethodValidator> closureMethodValidatorMap;
    private final TransactionsPort transactionsPort;

    private static final Logger logger = LoggerFactory.getLogger(ProductsBasicInfoHandler.class);
    private static final String CLOSED_STATE = "CLOSED";

    @Override
    public Response<ProductsBasicInfo> handle(Client client) {
        Response response;
        List<Credit> credits;
        ClientEntity clientEntity;
        List<SavingsAccount> savingsAccounts;
        ProductsBasicInfo productsBasicInfo = new ProductsBasicInfo();

        try {
            clientEntity = getClientEntity(client);
            savingsAccounts = getSavingsAccounts(clientEntity);
            credits =
                    getCredits(clientEntity, getLoanDetails(client.getAuthorizationHeader(), clientEntity));

            productsBasicInfo.setAllSavingsAccountsCloseable(
                    validateIfAllAccountsAreCloseable.test(savingsAccounts, credits));
            productsBasicInfo.setCredits(credits);
            productsBasicInfo.setAvailableClosingMethods(getAvailableClosureMethods(savingsAccounts, getPendingTransactions(client.getAuthorizationHeader(), clientEntity.getIdClient())));
            productsBasicInfo.setSavingsAccounts(savingsAccounts);
            return new Response<>(productsBasicInfo);
        } catch (ProviderException ex) {
            logger.error("Error service {}", ex.getMessage(), ex);
            response =
                    new Response<>(
                            getListValidations(
                                    MambuErrorsResultEnum.MAMBU_SERVICE_ERROR.name(), ex.getMessage()));
        } catch (ServiceException ex) {
            logger.error("Error in client products service {}", ex.getMessage(), ex);
            response =
                    new Response<>(getListValidations("Error in client products service ", ex.getMessage()));
        }
        return response;
    }

    private ClientEntity getClientEntity(Client client) {
        return clientsRepository
                .findByIdClient(client.getIdClient())
                .orElseThrow(
                        () ->
                                new ClientNotFoundException(
                                        ClientErrorResultsEnum.CLIENT_NOT_FOUND_IN_DB.name(), client.getIdClient()));
    }

    private List<SavingsAccount> getSavingsAccounts(ClientEntity clientEntity)
            throws ProviderException {
        GetAccountRequest getAccountRequest = new GetAccountRequest();

        getAccountRequest.setClientId(clientEntity.getIdCbs());
        List<GetAccountResponse> accounts = flexibilitySdk.getAccountsByClientId(getAccountRequest);

        return Optional.ofNullable(accounts).orElse(Collections.emptyList()).stream()
                .filter(account -> !CLOSED_STATE.equals(account.getState()))
                .map(this::getSavingsAccount)
                .collect(Collectors.toList());
    }

    public List<Credit> getCredits(
            ClientEntity clientEntity, LoanDetailRetrofitResponse loanDetailRetrofitResponse)
            throws ProviderException {
        List<Credit> credits = new ArrayList<>();
        List<LoanDetail> loanDetails = loanDetailRetrofitResponse.getContent();
        if (Objects.nonNull(loanDetails)) {
            for (LoanDetail loan : loanDetails) {
                if (!CLOSED_STATE.equals(loan.getState())) {
                    credits.add(getCredit(clientEntity.getIdCbs(), loan.getIdLoan()));
                }
            }
        }
        return credits;
    }

    private BiPredicate<List<SavingsAccount>, List<Credit>> validateIfAllAccountsAreCloseable =
            (savingsAccounts, credits) ->
                    (savingsAccounts.stream().allMatch(SavingsAccount::isSavingAccountClosable))
                            && credits.stream()
                            .allMatch(credit -> credit.getBalance().compareTo(BigDecimal.ZERO) == 0);

    private LoanDetailRetrofitResponse getLoanDetails(
            Map<String, String> headers, ClientEntity clientEntity) {
        return getLoanDetailOperationsService.getCreditsProducts(headers, clientEntity.getIdClient());
    }

    private SavingsAccount getSavingsAccount(GetAccountResponse accountResponse) {
        Double balance =
                Optional.ofNullable(accountResponse.getAvailableBalance())
                        .map(GetAccountResponse.Balance::getAmount)
                        .orElse(0D);
        BigDecimal interestAccrued = Optional.ofNullable(accountResponse.getInterestAccrued())
                        .map(interest -> MoneyUtil.roundTwoDigitsUp(BigDecimal.valueOf(interest.getAmount())))
                        .orElse(BigDecimal.ZERO);
        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setIdSavingAccount(accountResponse.getNumber());
        savingsAccount.setBalance(UtilValidators.doubleToBd(balance));
        savingsAccount.setCreateOn(accountResponse.getCreationDate().withNano(0));
        savingsAccount.setStartPeriodDate(accountResponse.getCreationDate().plusMonths(1L).withNano(0));
        savingsAccount.setLastPeriodDate(DatesUtil.getLocalDateGMT5().withNano(0));
        savingsAccount.setSavingAccountClosable(balance.compareTo(0d) == 0);
        savingsAccount.setGmf(Boolean.parseBoolean(accountResponse.getGmf()));
        savingsAccount.setInterestAccrued(interestAccrued);
        return savingsAccount;
    }

    private Credit getCredit(String idCbs, String loanId) throws ProviderException {
        GetLoanRequest getLoanRequest = new GetLoanRequest();
        getLoanRequest.setClientId(idCbs);
        getLoanRequest.setLoanId(loanId);
        GetLoanResponse loanResponse = flexibilitySdk.getLoanByLoanAccountId(getLoanRequest);

        Credit credit = new Credit();
        credit.setIdCredit(loanResponse.getId());
        credit.setBalance(UtilValidators.doubleToBd(loanResponse.getBalance().getAmount()));
        credit.setStartPeriodDate(loanResponse.getCreationDate().plusMonths(1L).withNano(0));
        credit.setLastPeriodDate(DatesUtil.getLocalDateGMT5().withNano(0));
        credit.setCreateOn(loanResponse.getCreationDate().withNano(0));
        return credit;
    }

    private List<String> getAvailableClosureMethods(
            List<SavingsAccount> savingsAccounts, boolean hasPendingTransactions) {

        List<String> availableMethods = savingsAccounts.stream()
                .findFirst()
                .map(savingsAccount -> {
                    savingsAccount.setCalculatedBalance(savingsAccount.calculateBalance());
                    return getClosureMethods(savingsAccount);
                })
                .orElse(new ArrayList<>());

        if (hasPendingTransactions)
            availableMethods.add(TransactionClosureMethods.PENDING_TRANSACTIONS.name());

        setCardlessWithdrawalAmount(availableMethods, savingsAccounts);

        return availableMethods;
    }

    private List<String> getClosureMethods(SavingsAccount savingsAccount) {
        return Stream.of(BalanceClosureMethods.values())
                .filter(method -> closureMethodValidatorMap.get(method.name()).validateIfMethodApplies(savingsAccount))
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    private void setCardlessWithdrawalAmount(
            List<String> availableMethods, List<SavingsAccount> savingsAccounts) {
        availableMethods.stream()
                .filter(method -> BalanceClosureMethods.CARDLESS_WITHDRAWAL.name().equals(method))
                .forEach(
                        method ->
                                savingsAccounts.stream()
                                        .findFirst()
                                        .ifPresent(savingsAccount -> savingsAccount.setCardlessAmount(savingsAccount.getCalculatedBalance())));
    }

    private boolean getPendingTransactions(Map<String, String> headers, String idClient) {
        return transactionsPort.hasPendingTransactions(headers,
                idClient).getOrElseThrow(() -> new ServiceException("Error getting pending transactions"));
    }
}
