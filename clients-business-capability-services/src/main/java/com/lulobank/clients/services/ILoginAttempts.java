package com.lulobank.clients.services;

import com.lulobank.clients.services.features.login.model.AttemptTimeResult;
import com.lulobank.clients.services.outboundadapters.model.AttemptEntity;
import com.lulobank.clients.services.outboundadapters.model.LoginAttemptsEntity;

public interface ILoginAttempts {

  AttemptEntity saveLoginAttempt(String idClient, boolean isLoginSuccess);

  AttemptEntity savePasswordAttempt(String idClient, boolean isLoginSuccess);

  LoginAttemptsEntity resetFailedAttempts(String idClient);

  AttemptTimeResult getAttemptTimeFromAttemptEntity(AttemptEntity attemptEntity);

  AttemptEntity getLastDateFailedAttempt(String idClient);

  boolean isBlockedLogin(String idClient);

  String getAttemptTimeResult(AttemptTimeResult attemptTimeResult);

  String getLastSuccessfulLoginAttemptDate(String idClient);
}
