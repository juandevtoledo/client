package com.lulobank.clients.starter.config;

import com.lulobank.clients.starter.utils.LuloMockRestServer;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockWebServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Configuration
public class TestConfig {
  @Bean
  public LuloMockRestServer mockWebServer() {
    return new LuloMockRestServer(new MockWebServer());
  }

  @Bean
  @Primary
  public Retrofit luloTestRetrofit(OkHttpClient okHttpClient, LuloMockRestServer mockWebServer) {
    return new Retrofit.Builder()
        .baseUrl(mockWebServer.getMockWebServer().url("/"))
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build();
  }
}
