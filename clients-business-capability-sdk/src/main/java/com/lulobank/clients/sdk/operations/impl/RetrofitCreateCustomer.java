package com.lulobank.clients.sdk.operations.impl;

import com.lulobank.clients.sdk.operations.CreateCustomerOperation;
import com.lulobank.clients.sdk.operations.exception.CreateCustomerServiceException;
import com.lulobank.utils.client.retrofit.RetrofitFactory;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.HeaderMap;
import retrofit2.http.PUT;
import retrofit2.http.Path;

import java.util.Map;
import java.util.function.Predicate;

@Slf4j
public class RetrofitCreateCustomer implements CreateCustomerOperation {

    public static final String REQUEST_FAILED = "Request failed";
    public static final String CREATE_ACCEPTANCES_DOCUMENT = "Error consuming service for customer sac creation. Endpoint " +
            "clients/%s/create-customer. Cause: %s";

    private final Retrofit retrofit;
    private final RetrofitCreateCustomerService service;

    public RetrofitCreateCustomer(String url) {
        this.retrofit = RetrofitFactory.buildRetrofit(url);
        this.service = this.retrofit.create(RetrofitCreateCustomer.RetrofitCreateCustomerService.class);
    }

    @Override
    public Try<Boolean> createCustomer(Map<String, String> headers, String idClient) {
        return Try.of(() -> service.createCustomer(headers, idClient))
                .mapTry(Call::execute)
                .map(this::processResponse)
                .map(response -> true)
                .onFailure(e -> log.error(String.format(CREATE_ACCEPTANCES_DOCUMENT, idClient, e.getMessage())));
    }

    private <T> T processResponse(Response<T> response) {
        if (response.isSuccessful())
            return response.body();

        String errorBody = Try.of(response::errorBody)
                .mapTry(ResponseBody::string)
                .filter(isNotEmpty)
                .getOrElse(REQUEST_FAILED);
        throw new CreateCustomerServiceException(String.format("STATUS CODE: %s. %s", response.code(), errorBody), response.code());
    }

    private final Predicate<String> isNotEmpty = text -> text != null && text.length() > 0;

    interface RetrofitCreateCustomerService {
        @PUT("clients/{idClient}/create-customer")
        Call<Void> createCustomer(@HeaderMap Map<String, String> headers, @Path("idClient") String idClient);
    }
}