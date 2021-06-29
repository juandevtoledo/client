package com.lulobank.clients.starter.v3.adapters.out.savingsaccountservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulobank.clients.services.application.util.HttpDomainStatus;
import com.lulobank.clients.starter.v3.adapters.in.dto.GenericResponse;
import com.lulobank.clients.starter.v3.adapters.out.savingsaccountservice.dto.CreateSavingsAccountRequest;
import com.lulobank.clients.starter.v3.adapters.out.savingsaccountservice.dto.SavingAccountCreated;
import com.lulobank.clients.starter.v3.adapters.out.savingsaccountservice.mapper.SavingsAccountV3AdapterMapper;
import com.lulobank.clients.v3.adapters.port.out.savingsaccount.SavingsAccountV3Service;
import com.lulobank.clients.v3.adapters.port.out.savingsaccount.dto.SavingsAccountRequest;
import com.lulobank.clients.v3.adapters.port.out.savingsaccount.dto.SavingsAccountResponse;
import com.lulobank.clients.v3.adapters.port.out.savingsaccount.error.SavingsAccountError;
import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.http.HttpStatus;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.Map;

import static com.lulobank.clients.v3.adapters.port.out.savingsaccount.error.SavingAccountServiceErrorStatus.CLI_140;

@Slf4j
public class SavingsAccountV3Adapter implements SavingsAccountV3Service {

    private ISavingsAccountRetrofitService service;
    private ObjectMapper objectMapper = new ObjectMapper();
    public SavingsAccountV3Adapter( String url) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.service = retrofit.create(ISavingsAccountRetrofitService.class);
    }

    @Override
    public Either<SavingsAccountError, SavingsAccountResponse> create(SavingsAccountRequest savingsAccountRequest, Map<String, String> auth) {
        Call<SavingAccountCreated> call = this.service.savingsaccountsCreated(auth, savingsAccountRequest.getIdClient(), SavingsAccountV3AdapterMapper.INSTANCE.toCreateSavingsAccountRequest(savingsAccountRequest));
        return Try.of(call::execute)
                .mapTry(response -> {
                    if (HttpStatus.CREATED.value() == response.code()) {
                        return Either.<SavingsAccountError, SavingsAccountResponse>right(SavingsAccountV3AdapterMapper.INSTANCE.toSavingsAccountResponse(response.body()));
                    } else {
                        GenericResponse genericResponse = objectMapper.readValue(response.errorBody().string(), GenericResponse.class);
                        return Either.<SavingsAccountError, SavingsAccountResponse>left(SavingsAccountV3AdapterMapper.INSTANCE.toSavingsAccountError(genericResponse.getError()));
                    }
                }).recover(error -> {
                    log.error("Error trying connect to SavingAccount Service, idClient : {}, msg : {} ", savingsAccountRequest.getIdClient(), error.getMessage());
                    return Either.left(new SavingsAccountError(CLI_140.name(),String.valueOf(HttpDomainStatus.INTERNAL_SERVER_ERROR.value())));
                }).get();
    }

    private interface ISavingsAccountRetrofitService {
        @POST("/savingsaccounts/v3/client/{idClient}/create")
        Call<SavingAccountCreated> savingsaccountsCreated(@HeaderMap Map<String, String> headers,
                                                          @Path("idClient") String idClient,
                                                          @Body CreateSavingsAccountRequest createSavingsAccountRequest);
    }
}
