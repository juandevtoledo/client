package com.lulobank.clients.sdk.operations.impl;

import static com.lulobank.utils.client.retrofit.oauth.OAuth2RetrofitFactory.buildRetrofit;

import com.lulobank.utils.client.retrofit.oauth.ITokenManager;

public class RetrofitClientsOperationsOAuth2 extends RetrofitClientOperations {

  public RetrofitClientsOperationsOAuth2(
      String url,
      String clientId,
      String clientSecret,
      String clientUrl,
      ITokenManager tokenManager) {
    super(url);
    this.retrofit = buildRetrofit(url, clientId, clientSecret, clientUrl, tokenManager);
    this.service = this.retrofit.create(RetrofitClientOperations.RetrofitClientServices.class);
  }
}
