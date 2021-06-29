package com.lulobank.clients.starter.outboundadapter.authentication;

import brave.http.HttpTracing;
import brave.okhttp3.TracingInterceptor;
import com.lulobank.authentication.sdk.operations.impl.RetrofitClientTokenOperations;
import com.lulobank.clients.services.ports.out.AuthenticationService;
import io.vavr.control.Try;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Configuration
public class AuthenticationServiceConfig {

    @Value("${services.authentication.url}")
    private String serviceDomain;

    @Bean
    public AuthenticationService getAuthenticationService(@Qualifier("authentication") Retrofit retrofit){
        return new AuthenticationServiceAdapter(new RetrofitClientTokenOperations(retrofit));
    }

    @Bean("authentication")
    public Retrofit promissoryRetrofit(HttpTracing tracing) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        Try.of(() -> TracingInterceptor.create(tracing))
                .peek(builder::addNetworkInterceptor);
        return new Retrofit.Builder()
                .baseUrl(serviceDomain)
                .client( builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }




}
