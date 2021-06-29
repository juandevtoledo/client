package com.lulobank.clients.sdk.operations.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lulobank.clients.sdk.operations.IClientInformationOperations;
import com.lulobank.clients.sdk.operations.dto.onboardingclients.ClientInformationByIdClient;
import com.lulobank.clients.sdk.operations.util.LocalDateDeserializer;
import com.lulobank.clients.sdk.operations.util.RetrofitsUtil;
import com.lulobank.core.Response;
import com.lulobank.utils.exception.ServiceException;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Path;

@Slf4j
public class RetrofitClientInformationOperations implements IClientInformationOperations {
  private Retrofit retrofit;
  private ClientInformationServices service;
  private static final String ERROR_BODY_NULL = "response body is null";

  public RetrofitClientInformationOperations(String url) {
    Gson gson =
        new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
            .create();
    this.retrofit =
        new Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();
    this.service = this.retrofit.create(ClientInformationServices.class);
  }

  @Override
  public ClientInformationByIdClient getAllClientInformationByIdClient(
      Map<String, String> headers, String idClient) {
    Call callService = service.getAllClientInformationByIdClient(headers, idClient);
    try {
      retrofit2.Response response = callService.execute();
      ResponseEntity<Response<ClientInformationByIdClient>> responseEntity =
          new RetrofitsUtil<>().getResponseEntity(response);
      return Optional.ofNullable(responseEntity.getBody())
          .orElseThrow(() -> new ServiceException(ERROR_BODY_NULL))
          .getContent();
    } catch (ServiceException e) {
      log.error("Error validations /idclient/{idClient} REST service: " + idClient, e);
      throw e;
    } catch (Exception e) {
      log.error("Error validations /idclient/{idClient} REST service: " + idClient, e);
      throw new ServiceException(
          HttpStatus.INTERNAL_SERVER_ERROR.value(),
          HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
          e);
    }
  }

  private interface ClientInformationServices {
    @GET("clients/idClient/{idClient}")
    Call<Response<ClientInformationByIdClient>> getAllClientInformationByIdClient(
        @HeaderMap Map<String, String> headers, @Path("idClient") String idClient);
  }
}
