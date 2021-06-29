package com.lulobank.clients.sdk.operations.impl;

import com.lulobank.clients.sdk.operations.IClientValidationOperations;
import com.lulobank.utils.client.retrofit.RetrofitFactory;
import com.lulobank.utils.exception.ServiceException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;

public class RetrofitClientValidationOperations implements IClientValidationOperations {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(RetrofitClientValidationOperations.class);

  private static final String ERROR_MESSAGE = "ERROR";

  private Retrofit retrofit;

  public RetrofitClientValidationOperations(String url) {
    super();
    this.retrofit = RetrofitFactory.buildRetrofit(url);
  }

  protected RetrofitClientValidationServices getRetrofitClientValidationServices() {
    return this.getRetrofit().create(RetrofitClientValidationServices.class);
  }

  protected Retrofit getRetrofit() {
    return this.retrofit;
  }

  @Override
  public ResponseEntity<String> validateEmail(Map<String, String> headers, String email) {

    RetrofitClientValidationServices service = this.getRetrofitClientValidationServices();
    Call<Void> p = service.validateEmail(headers, email);
    try {
      Response<Void> response = p.execute();

      return getStringResponseEntity(response);
    } catch (ServiceException e) {
      throw e;
    } catch (Exception e) {
      LOGGER.error("Error clients/email/{email} REST service: " + email, e);
      throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), ERROR_MESSAGE, e);
    }
  }

  @Override
  public ResponseEntity<String> validatePhone(
      Map<String, String> headers, int country, BigInteger phone) {
    RetrofitClientValidationServices service = this.getRetrofitClientValidationServices();
    Call<Void> p = service.validatePhone(headers, country, phone);
    try {
      Response<Void> response = p.execute();

      return getStringResponseEntity(response);
    } catch (ServiceException e) {
      throw e;
    } catch (Exception e) {
      LOGGER.error("Error clients/phonenumber REST service: " + phone, e);
      throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), ERROR_MESSAGE, e);
    }
  }

  @Override
  public ResponseEntity<String> validateIdCard(Map<String, String> headers, String idCard) {
    RetrofitClientValidationServices service = this.getRetrofitClientValidationServices();
    Call<Void> p = service.validateIdCard(headers, idCard);
    try {
      Response<Void> response = p.execute();

      return getStringResponseEntity(response);
    } catch (ServiceException e) {
      throw e;
    } catch (Exception e) {
      LOGGER.error("Error validations/idcard/{idcard} REST service: " + idCard, e);
      throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), ERROR_MESSAGE, e);
    }
  }

  @NotNull
  public ResponseEntity<String> getStringResponseEntity(Response<Void> response)
      throws IOException {
    if (response.code() == HttpStatus.OK.value()) {
      return new ResponseEntity<>(HttpStatus.valueOf(response.code()));
    } else {
      String errorBody = response.errorBody().string();
      if(errorBody.isEmpty()){
        errorBody= ERROR_MESSAGE;
      }
      throw new ServiceException(response.code(), errorBody);
    }
  }

  interface RetrofitClientValidationServices {

    @GET("validations/email/{email}")
    Call<Void> validateEmail(@HeaderMap Map<String, String> headers, @Path("email") String email);

    @GET("validations/phonenumber")
    Call<Void> validatePhone(
        @HeaderMap Map<String, String> headers,
        @Query("country") int country,
        @Query("number") BigInteger phone);

    @GET("validations/idcard/{idcard}")
    Call<Void> validateIdCard(
        @HeaderMap Map<String, String> headers, @Path("idcard") String idCard);
  }
}
