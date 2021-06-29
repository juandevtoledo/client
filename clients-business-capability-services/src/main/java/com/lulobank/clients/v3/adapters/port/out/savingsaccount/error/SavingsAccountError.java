package com.lulobank.clients.v3.adapters.port.out.savingsaccount.error;

import com.lulobank.clients.services.application.util.HttpDomainStatus;
import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import lombok.Getter;
import lombok.Setter;

import static com.lulobank.clients.v3.adapters.port.out.savingsaccount.error.SavingAccountServiceErrorStatus.CLI_140;
import static com.lulobank.clients.v3.adapters.port.out.savingsaccount.error.SavingAccountServiceErrorStatus.CLI_141;
import static com.lulobank.clients.v3.adapters.port.out.savingsaccount.error.SavingAccountServiceErrorStatus.CLI_142;

@Getter
@Setter
public class SavingsAccountError extends UseCaseResponseError {

    public SavingsAccountError(){
        super();
    };

    public SavingsAccountError(String code, String failure) {
        super(code,failure,SavingAccountServiceErrorStatus.DEFAULT_DETAIL);
    }

    public SavingsAccountError(String code, String failure,String detail) {
        super(code,failure,detail);
    }

    public static SavingsAccountError connectionError(){
        return new SavingsAccountError(CLI_140.name(),String.valueOf(HttpDomainStatus.INTERNAL_SERVER_ERROR.value()));
    }

    public static SavingsAccountError checkReferralHoldError(){
        return new SavingsAccountError(CLI_141.name(),String.valueOf(HttpDomainStatus.INTERNAL_SERVER_ERROR.value()));
    }

    public static SavingsAccountError clientDoNotHaveProduct() {
        return new SavingsAccountError(CLI_142.name(), String.valueOf(HttpDomainStatus.BAD_REQUEST));
    }
}
