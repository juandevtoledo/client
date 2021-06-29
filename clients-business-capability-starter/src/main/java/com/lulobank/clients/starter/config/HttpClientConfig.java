package com.lulobank.clients.starter.config;


import lombok.Getter;
import lombok.Setter;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
@Configuration
public class HttpClientConfig {
  @Value("${http.client.connectionTimeout:30000}")
  private Long connectionTimeout;

  @Value("${http.client.readtimeout:30000}")
  private Long readTimeout;

  @Value("${http.client.writeTimeout:30000}")
  private Long writeTimeout;

  @Value("${http.client.baseUrl:http://mock.local.com/}")
  private String baseUrl;

  @Bean
  @Profile({"default", "stg"})
  @Order(value = 10)
  public Interceptor httpClientLoggingInterceptor() {
    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    return interceptor;
  }

  @Bean
  @ConditionalOnMissingBean
  public Retrofit luloRetrofit(OkHttpClient okHttpClient) {
    return new Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build();
  }

  @Bean
  @ConditionalOnMissingBean
  public OkHttpClient luloOkHttpClient(@Autowired List<Interceptor> interceptors) {
    OkHttpClient.Builder builder =
        new OkHttpClient.Builder()
            .connectTimeout(connectionTimeout, TimeUnit.MILLISECONDS)
            .writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)
            .readTimeout(readTimeout, TimeUnit.MILLISECONDS);
    interceptors.forEach(builder::addNetworkInterceptor);
    return builder.build();
  }
}
