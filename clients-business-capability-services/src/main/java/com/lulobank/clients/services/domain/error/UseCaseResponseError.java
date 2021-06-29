package com.lulobank.clients.services.domain.error;

import com.lulobank.clients.services.application.util.HttpDomainStatus;
import com.lulobank.clients.v3.error.UseCaseErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UseCaseResponseError {
    private String businessCode;
    private String providerCode;
    private String detail = UseCaseErrorStatus.DEFAULT_DETAIL;

    public UseCaseResponseError(String businessCode, String providerCode) {
        this.businessCode = businessCode;
        this.providerCode = providerCode;
    }

    public UseCaseResponseError(String businessCode, Integer providerCode, String detail) {
        this.businessCode = businessCode;
        this.providerCode = String.valueOf(providerCode);
        this.detail = detail;
    }

    public UseCaseResponseError(String businessCode, HttpDomainStatus httpDomainStatus, String detail) {
        this.businessCode = businessCode;
        this.providerCode = String.valueOf(httpDomainStatus.value());
        this.detail = detail;
    }

    public static  <T extends UseCaseResponseError> UseCaseResponseError map(T t){
        return new UseCaseResponseError(t.getBusinessCode(),t.getProviderCode(),t.getDetail());
    }

}
