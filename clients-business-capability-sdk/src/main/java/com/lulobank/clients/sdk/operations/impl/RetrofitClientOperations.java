package com.lulobank.clients.sdk.operations.impl;

import com.lulobank.clients.sdk.operations.IClientOperations;
import com.lulobank.clients.sdk.operations.dto.ClientInformationByIdCard;
import com.lulobank.clients.sdk.operations.dto.ClientInformationByIdClient;
import com.lulobank.clients.sdk.operations.dto.ClientInformationByPhone;
import com.lulobank.clients.sdk.operations.dto.ClientInformationByTypeResponse;
import com.lulobank.clients.sdk.operations.dto.ClientSuccessResult;
import com.lulobank.clients.sdk.operations.dto.DemographicInfoByIdClient;
import com.lulobank.clients.sdk.operations.dto.UpdateClientAddressRequest;
import com.lulobank.clients.sdk.operations.dto.VerifyEmailResponse;
import com.lulobank.clients.sdk.operations.dto.exception.GetClientInfoException;
import com.lulobank.clients.sdk.operations.exception.UpdateClientInformationException;
import com.lulobank.utils.client.retrofit.RetrofitFactory;
import com.lulobank.utils.exception.ServiceException;
import io.vavr.control.Option;
import io.vavr.control.Try;

import java.io.IOException;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.PUT;
import retrofit2.http.GET;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;


public class RetrofitClientOperations implements IClientOperations {

    private static final Logger LOGGER = LoggerFactory.getLogger(RetrofitClientOperations.class);

    private static final String ERROR_MESSAGE = "ERROR";
    private static final String ERROR_MESSAGE_FIND_CLIENT_BY_CARD_ID =
            "Error clients service getClientInformationByIdCardInternal";

    private static final String ERROR_MESSAGE_FIND_CLIENT_BY_TYPE_SEARCH =
            "Error clients Service getClientByType";

    protected Retrofit retrofit;

    protected RetrofitClientServices service;

    public RetrofitClientOperations(String url) {
        this.retrofit = RetrofitFactory.buildRetrofit(url);
        this.service = this.retrofit.create(RetrofitClientServices.class);
    }

    public RetrofitClientOperations(Retrofit retrofit) {
        this.retrofit = retrofit;
        this.service = this.retrofit.create(RetrofitClientServices.class);
    }

    @Override
    public ClientInformationByIdClient getClientByIdClient(
            Map<String, String> headers, String idClient) {
        Call<ClientInformationByIdClient> p = service.getClientByIdClient(headers, idClient);
        try {
            Response<ClientInformationByIdClient> response = p.execute();

            if (response.body() != null) {
                LOGGER.debug("getClientByIdClient GET: {}", response.body());
            }
            return getClientInformationByIdClientEntity(response);

        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error validations /idclient/{idClient} REST service: " + idClient, e);
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), ERROR_MESSAGE, e);
        }
    }

    @Override
    public ClientInformationByPhone getClientByPhoneNumber(
            Map<String, String> headers, int country, String number) {
        Call<ClientInformationByPhone> p = service.getClientByPhoneNumber(headers, country, number);
        return getClientInformationByPhone(country, number, p, "getClientByPhoneNumber GET: {}");
    }

    @NotNull
    private ClientInformationByPhone getClientInformationByPhone(
            int country, String number, Call<ClientInformationByPhone> p, String s) {
        try {
            Response<ClientInformationByPhone> response = p.execute();

            if (response.body() != null) {
                LOGGER.info(s, response.body());
            }
            return getClientInformationByPhoneEntity(response);

        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(
                    "Error validations clients/phonenumber REST service: " + country + " - " + number, e);
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), ERROR_MESSAGE, e);
        }
    }

    @Override
    public ClientInformationByPhone getClientByPhoneNumberInternal(
            Map<String, String> headers, int country, String number) {
        Call<ClientInformationByPhone> p =
                service.getClientByPhoneNumberInternal(headers, country, number);
        return getClientInformationByPhone(
                country, number, p, "getClientByPhoneNumberInternal GET: {}");
    }

    @Override
    public VerifyEmailResponse verifyEmailClientInformation(
            Map<String, String> headers, String email) {
        Call<VerifyEmailResponse> p = service.verifyEmailClientInformation(headers, email);
        try {
            Response<VerifyEmailResponse> response = p.execute();

            if (response.body() != null) {
                LOGGER.info("verifyEmailClientInformation GET: {}", response.body());
            }
            return getVerifyEmailResponseEntity(response);

        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error validations /profile/email/verify REST service: " + email, e);
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), ERROR_MESSAGE, e);
        }
    }

    @Override
    public boolean updateClientInformation(
            Map<String, String> headers, UpdateClientAddressRequest updateClientRequest) {
        Call<Void> callService = service.updateClientInformation(headers, updateClientRequest);
        try {
            Response<Void> response = callService.execute();
            return this.getClientResult(response);
        } catch (ServiceException | IOException e) {
            LOGGER.error(
                    "Error validations /profile/update REST service: " + Encode.forJava(updateClientRequest.getIdClient()),
                    e);
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), ERROR_MESSAGE, e);
        }
    }

    @Override
    public Try<Boolean> updateClientInformationV2(
            Map<String, String> headers, UpdateClientAddressRequest updateClientRequest, String clientId) {
        Call<Void> callService = service.updateClientInformationV2(headers, updateClientRequest, clientId);
        return Try.of(() -> {
            Response<Void> response = callService.execute();
            return this.getClientResult(response);
        }).onFailure(e -> {
            LOGGER.error("Error validations clients/{idClient}/profile REST service: " + clientId, e);
            throw new UpdateClientInformationException("Error validations clients/{idClient}/profile REST service: " + clientId,e);
        });


    }

    /**
     * @deprecated (see ClientsDemographicAdapterV3
     * new url /api/v3/client/{idClient}/demographic) 
     */
    @Deprecated
    @Override
    public DemographicInfoByIdClient getDemographicInfoByClient(
            Map<String, String> headers, String idClient) {

        Call p = service.getDemographicInfoByClient(headers, idClient);
        try {
            Response<ClientSuccessResult<DemographicInfoByIdClient>> response = p.execute();

            if (response.body() != null) {
                LOGGER.info("getDemographicInfoByClient GET: {}", response.body());
            }
            return getDemographicInfoByIdClientEntity(response);

        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error validations /demographic/{idClient} REST service: " + Encode.forJava(idClient), e);
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), ERROR_MESSAGE, e);
        }
    }

    @Override
    public ClientInformationByIdCard getClientInformationByIdCard(
            Map<String, String> headers, String idCard) {
        Call<ClientSuccessResult<ClientInformationByIdCard>> p =
                service.getClientInformationByIdCard(headers, idCard);

        try {
            Response<ClientSuccessResult<ClientInformationByIdCard>> response = p.execute();

            if (response.body() != null) {
                LOGGER.info("getClientInformationByIdCard GET: {}", response.body());
            }
            return getClientInformationByIdCardEntity(response);

        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error validations clients/idCard REST service: " + idCard, e);
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), ERROR_MESSAGE, e);
        }
    }

    @Override
    public ClientInformationByIdCard getClientInformationByIdCardInternal(
            Map<String, String> headers, String cardId) {
        Call<ClientSuccessResult<ClientInformationByIdCard>> p =
                service.getClientInformationByIdCardInternal(headers, cardId);

        return (Try.of(() -> getClientInformationByIdCard(p))
                .onFailure(
                        throwable ->
                                handleGetClientInformationByIdCardInternalException(throwable, cardId)))
                .get();
    }

    @NotNull
    private ClientInformationByIdCard getClientInformationByIdCard(
            Call<ClientSuccessResult<ClientInformationByIdCard>> p) throws IOException {
        Response<ClientSuccessResult<ClientInformationByIdCard>> response = p.execute();

        if (response.body() != null) {
            LOGGER.info("getClientInformationByIdCardInternal GET: {}", response.body());
        }
        return getClientInformationByIdCardEntityInternal(response);
    }

    private void handleGetClientInformationByIdCardInternalException(Throwable e, String cardId) {
        LOGGER.error(ERROR_MESSAGE_FIND_CLIENT_BY_CARD_ID, cardId, e);
        throw new GetClientInfoException(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), ERROR_MESSAGE_FIND_CLIENT_BY_CARD_ID, e);
    }

    @Override
    public ClientInformationByTypeResponse getClientByType(
            Map<String, String> headers, String searchType, String value) {

        Call<ClientSuccessResult<ClientInformationByTypeResponse>> p =
                service.getClientByType(headers, searchType, value);

        return (Try.of(() -> getClientInformation(p))
                .onFailure(throwable -> handleGetClientInformationByTypeSearch(throwable, value)))
                .get();
    }

    @Override
    public ClientInformationByTypeResponse getClientByTypeInternal(
            Map<String, String> headers, String searchType, String value) {
        Call<ClientSuccessResult<ClientInformationByTypeResponse>> p =
                service.getClientByTypeInternal(headers, searchType, value);

        return (Try.of(() -> getClientInformation(p))
                .onFailure(throwable -> handleGetClientInformationByTypeSearch(throwable, value)))
                .get();
    }

    private ClientInformationByTypeResponse getClientInformation(
            Call<ClientSuccessResult<ClientInformationByTypeResponse>> p) throws IOException {
        Response<ClientSuccessResult<ClientInformationByTypeResponse>> response = p.execute();
        if (response.body() != null) {
            LOGGER.debug("getClientInformationByTypeSearch GET: {}", response.body());
        }
        return getClientInformationByTypeResponseEntity(response);
    }

    private void handleGetClientInformationByTypeSearch(Throwable e, String value) {
        LOGGER.error(ERROR_MESSAGE_FIND_CLIENT_BY_TYPE_SEARCH, value, e);
        throw new GetClientInfoException(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), ERROR_MESSAGE_FIND_CLIENT_BY_TYPE_SEARCH, e);
    }

    @NotNull
    public ClientInformationByIdClient getClientInformationByIdClientEntity(
            Response<ClientInformationByIdClient> response) throws IOException {
        if (response.code() == HttpStatus.BAD_REQUEST.value()) {
            String errorBody = response.errorBody() != null ? response.errorBody().string() : "";
            throw new ServiceException(response.code(), errorBody);
        }
        return response.body();
    }

    @NotNull
    public ClientInformationByPhone getClientInformationByPhoneEntity(
            Response<ClientInformationByPhone> response) throws IOException {
        if (response.code() == HttpStatus.BAD_REQUEST.value()) {
            String errorBody = response.errorBody() != null ? response.errorBody().string() : "";
            throw new ServiceException(response.code(), errorBody);
        }
        return response.body();
    }

    @NotNull
    public ClientInformationByIdCard getClientInformationByIdCardEntity(
            Response<ClientSuccessResult<ClientInformationByIdCard>> response) throws IOException {
        if (HttpStatus.OK.value() != response.code()) {
            String errorBody = response.errorBody() != null ? response.errorBody().string() : "";
            throw new ServiceException(response.code(), errorBody);
        }
        return response.body().getContent();
    }

    @NotNull
    public ClientInformationByIdCard getClientInformationByIdCardEntityInternal(
            Response<ClientSuccessResult<ClientInformationByIdCard>> response) throws IOException {
        if (HttpStatus.OK.value() != response.code()) {
            String errorBody = response.errorBody() != null ? response.errorBody().string() : "";
            throw new GetClientInfoException(response.code(), errorBody);
        }
        return response.body().getContent();
    }

    @NotNull
    public VerifyEmailResponse getVerifyEmailResponseEntity(Response<VerifyEmailResponse> response)
            throws IOException {
        if (response.code() == HttpStatus.BAD_REQUEST.value()) {
            String errorBody = response.errorBody() != null ? response.errorBody().string() : "";
            throw new ServiceException(response.code(), errorBody);
        }
        return response.body();
    }

    @NotNull
    public boolean getClientResult(Response<Void> response) {

        if (response.code() != HttpStatus.OK.value()) {
            String errorBody = response.errorBody() != null ? response.errorBody().toString() : "";
            throw new ServiceException(response.code(), errorBody);
        }
        return true;
    }
    
    /**
     * @deprecated (see ClientsDemographicAdapterV3
     * new url /api/v3/client/{idClient}/demographic) 
     */
    @Deprecated
    public DemographicInfoByIdClient getDemographicInfoByIdClientEntity(
            Response<ClientSuccessResult<DemographicInfoByIdClient>> response) throws IOException {
        if (response.code() == HttpStatus.BAD_REQUEST.value()) {
            String errorBody = response.errorBody() != null ? response.errorBody().string() : "";
            throw new ServiceException(response.code(), errorBody);
        }
        return response.body().getContent();
    }

    @NotNull
    public ClientInformationByTypeResponse getClientInformationByTypeResponseEntity(
            Response<ClientSuccessResult<ClientInformationByTypeResponse>> response) throws IOException {
        if (HttpStatus.OK.value() != response.code()) {
            String errorBody = response.errorBody() != null ? response.errorBody().string() : "";
            throw new GetClientInfoException(response.code(), errorBody);
        }
        return Option.of(response.body())
                .map(clientSuccessResult -> clientSuccessResult.getContent())
                .get();
    }

    interface RetrofitClientServices {

        @GET("clients/idClient/{idClient}")
        Call<ClientInformationByIdClient> getClientByIdClient(
                @HeaderMap Map<String, String> headers, @Path("idClient") String idCLient);

        @GET("clients/phonenumber")
        Call<ClientInformationByPhone> getClientByPhoneNumber(
                @HeaderMap Map<String, String> headers,
                @Query("country") int country,
                @Query("number") String number);

        @GET("clients/internalPhonenumber")
        Call<ClientInformationByPhone> getClientByPhoneNumberInternal(
                @HeaderMap Map<String, String> headers,
                @Query("country") int country,
                @Query("number") String number);

        @GET("clients/profile/email/verify/{email}")
        Call<VerifyEmailResponse> verifyEmailClientInformation(
                @HeaderMap Map<String, String> headers, @Path("email") String email);

        @PUT("clients/{idClient}/profile")
        Call<Void> updateClientInformationV2(
                @HeaderMap Map<String, String> headers,
                @Body UpdateClientAddressRequest updateClientRequest, @Path("idClient") String idClient);

        @POST("clients/profile/update")
        Call<Void> updateClientInformation(
                @HeaderMap Map<String, String> headers,
                @Body UpdateClientAddressRequest updateClientRequest);
        
        /**
         * @deprecated (see ClientsDemographicAdapterV3
         * new url /api/v3/client/{idClient}/demographic) 
         */
        @Deprecated
        @GET("clients/demographic/{idClient}")
        Call<ClientSuccessResult<DemographicInfoByIdClient>> getDemographicInfoByClient(
                @HeaderMap Map<String, String> headers, @Path("idClient") String idClient);

        @GET("clients/idCard")
        Call<ClientSuccessResult<ClientInformationByIdCard>> getClientInformationByIdCard(
                @HeaderMap Map<String, String> headers, @Query("idCard") String idCard);

        @GET("clients/idCardInternal")
        Call<ClientSuccessResult<ClientInformationByIdCard>> getClientInformationByIdCardInternal(
                @HeaderMap Map<String, String> headers, @Query("idCard") String idCard);

        @GET("clients/searchType")
        Call<ClientSuccessResult<ClientInformationByTypeResponse>> getClientByType(
                @HeaderMap Map<String, String> headers,
                @Query("searchType") String searchType,
                @Query("value") String value);

        @GET("clients/searchTypeInternal")
        Call<ClientSuccessResult<ClientInformationByTypeResponse>> getClientByTypeInternal(
                @HeaderMap Map<String, String> headers,
                @Query("searchType") String searchType,
                @Query("value") String value);
    }
}
