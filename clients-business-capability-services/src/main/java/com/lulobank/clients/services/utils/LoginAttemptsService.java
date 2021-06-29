package com.lulobank.clients.services.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulobank.clients.services.ILoginAttempts;
import com.lulobank.clients.services.features.login.model.AttemptTimeResult;
import com.lulobank.clients.services.outboundadapters.model.AttemptEntity;
import com.lulobank.clients.services.outboundadapters.model.LoginAttemptsEntity;
import com.lulobank.clients.services.outboundadapters.repository.LoginAttemptsRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginAttemptsService implements ILoginAttempts {

  public static final String LOGIN_ATTEMPTS_TABLE = "LoginAttempts";

  private final LoginAttemptsRepository loginAttemptsRepository;

  private Logger logger = LoggerFactory.getLogger(LoginAttemptsService.class);

  private Map<Integer, String> penaltyMap;

  private Integer maxAttemptsAllowed;

  public LoginAttemptsService(LoginAttemptsRepository loginAttemptsRepository, Map penaltyMap) {
    this.loginAttemptsRepository = loginAttemptsRepository;
    this.penaltyMap = penaltyMap;
    this.maxAttemptsAllowed = Integer.parseInt(Collections.max(penaltyMap.keySet()).toString());
  }

  public AttemptEntity saveLoginAttempt(String idClient, boolean isLoginSuccess) {

    LoginAttemptsEntity loginAttempt = null;
    List<AttemptEntity> attemptEntities = new ArrayList<>();
    AttemptEntity attemptEntity = null;
    Optional<LoginAttemptsEntity> loginAttempts =
        loginAttemptsRepository.findByIdClient(UUID.fromString(idClient));

    if (!loginAttempts.isPresent()) {

      loginAttempt = new LoginAttemptsEntity();
      loginAttempt.setIdClient(UUID.fromString(idClient));
      attemptEntity = new AttemptEntity(AttemptPenaltyEnum.ATTEMPT.getMinutes(), Boolean.FALSE);
      attemptEntities.add(attemptEntity);

      if (isLoginSuccess) {
        loginAttempt.setSuccessfulAttempt(attemptEntities);
        loginAttempt.setFailsAttempt(new ArrayList<>());
      } else {
        loginAttempt.setFailsAttempt(attemptEntities);
        loginAttempt.setSuccessfulAttempt(new ArrayList<>());
      }

    } else {
      loginAttempt = loginAttempts.get();
      if (isLoginSuccess) {
        attemptEntities = new ArrayList<>();
        attemptEntity =
            new AttemptEntity(
                AttemptPenaltyEnum.ATTEMPT.getMinutes(), isMaxAttemptAllowed(loginAttempt));
        attemptEntities.add(attemptEntity);
        loginAttempt.setSuccessfulAttempt(attemptEntities);
        loginAttempt.setFailsAttempt(new ArrayList<>());
      } else {
        attemptEntity = validateLoginAttempts(loginAttempt);
        attemptEntities =
            Objects.isNull(loginAttempt.getFailsAttempt())
                ? new ArrayList<AttemptEntity>()
                : loginAttempt.getFailsAttempt();
        attemptEntities.add(attemptEntity);
        loginAttempt.setFailsAttempt(attemptEntities);
      }
    }
    loginAttemptsRepository.save(loginAttempt);
    return attemptEntity;
  }

  public AttemptEntity savePasswordAttempt(String idClient, boolean isLoginSuccess) {
    LoginAttemptsEntity loginAttempt = getLoginAttemptEntity(idClient);
    AttemptEntity attemptEntity = validateLoginAttempts(loginAttempt);

    if (!isLoginSuccess) {
      if (Objects.isNull(loginAttempt.getFailsAttempt())) {
        loginAttempt.setFailsAttempt(new ArrayList<>());
      }
      loginAttempt.getFailsAttempt().add(attemptEntity);
    } else {
      loginAttempt.getFailsAttempt().clear();
      attemptEntity.setPenalty(0d);
    }
    loginAttemptsRepository.save(loginAttempt);
    return attemptEntity;
  }

  public LoginAttemptsEntity resetFailedAttempts(String idClient) {
    LoginAttemptsEntity loginAttemptsEntity = getLoginAttemptEntity(idClient);
    loginAttemptsEntity.getFailsAttempt().clear();
    loginAttemptsRepository.save(loginAttemptsEntity);
    return loginAttemptsEntity;
  }

  private LoginAttemptsEntity getLoginAttemptEntity(String idClient) {
    Optional<LoginAttemptsEntity> loginAttempts =
        loginAttemptsRepository.findByIdClient(UUID.fromString(idClient));
    return loginAttempts.orElseGet(
        () -> {
          LoginAttemptsEntity loginAttemptsEntity = new LoginAttemptsEntity();
          loginAttemptsEntity.setIdClient(UUID.fromString(idClient));
          loginAttemptsEntity.setFailsAttempt(new ArrayList<>());
          loginAttemptsEntity.setSuccessfulAttempt(new ArrayList<>());
          return loginAttemptsEntity;
        });
  }

  private AttemptEntity validateLoginAttempts(LoginAttemptsEntity loginAttempts) {
    Integer failedAttempts = getFailsAttempts(loginAttempts.getFailsAttempt());
    String penaltyTime = "0";

    try {
      penaltyTime = penaltyMap.get(failedAttempts);

      if (Objects.isNull(penaltyTime)) {
        penaltyTime = "0";
      }
    } catch (NoSuchElementException e) {
      logger.info("No penalty time");
    }

    return new AttemptEntity(Double.valueOf(penaltyTime), isMaxAttemptAllowed(loginAttempts));
  }

  private Integer getFailsAttempts(List<AttemptEntity> failsAttempt) {
    return Objects.isNull(failsAttempt) ? 0 : failsAttempt.size() + 1;
  }

  public AttemptTimeResult getAttemptTimeFromAttemptEntity(AttemptEntity attemptEntity) {
    AttemptTimeResult attemptTimeResult = new AttemptTimeResult();
    attemptTimeResult.setPenalty(attemptEntity.getPenalty());
    attemptTimeResult.setMaxAttempt(attemptEntity.getMaxAttempt());
    if (attemptEntity.getPenalty() > 0) {
      attemptTimeResult.setTimeRemaining(
          getPenaltyTimeRemaining(attemptEntity.getPenalty(), attemptEntity.getAttemptDate()));
    } else {
      attemptTimeResult.setTimeRemaining(0d);
    }
    return attemptTimeResult;
  }

  public AttemptEntity getLastDateFailedAttempt(String idClient) {
    AttemptEntity attemptEntity = null;
    LoginAttemptsEntity loginAttempts = getLoginAttemptEntity(idClient);
    Integer failedAttempts = getFailsAttempts(loginAttempts.getFailsAttempt());

    if (Objects.nonNull(loginAttempts.getFailsAttempt())
        && loginAttempts.getFailsAttempt().size() >= 1) {
      Double timeRemaining =
          getPenaltyTimeRemaining(
              loginAttempts
                  .getFailsAttempt()
                  .get(loginAttempts.getFailsAttempt().size() - 1)
                  .getPenalty(),
              loginAttempts
                  .getFailsAttempt()
                  .get(loginAttempts.getFailsAttempt().size() - 1)
                  .getAttemptDate());

      if (failedAttempts > maxAttemptsAllowed && timeRemaining > 0) {
        attemptEntity = new AttemptEntity(-1d, Boolean.FALSE);
      } else {
        attemptEntity =
            loginAttempts.getFailsAttempt().get(loginAttempts.getFailsAttempt().size() - 1);
      }
      attemptEntity.setMaxAttempt(isMaxAttemptAllowed(loginAttempts));
    }
    return attemptEntity;
  }

  public boolean isBlockedLogin(String idClient) {
    LocalDateTime now = LocalDateTime.now(ZoneId.of("America/Bogota"));
    AttemptEntity attemptEntity = getLastDateFailedAttempt(idClient);
    LoginAttemptsEntity loginAttemptsEntity = getLoginAttemptEntity(idClient);
    Integer failedAttempts = getFailsAttempts(loginAttemptsEntity.getFailsAttempt());

    if (failedAttempts > maxAttemptsAllowed) {
      return true;
    }

    if (Objects.nonNull(attemptEntity)) {
      LocalDateTime differenceDateTime =
          getDifferenceDateTimeInSeconds(
              attemptEntity.getAttemptDate(), attemptEntity.getPenalty());
      if (differenceDateTime.isAfter(now)) {
        return true;
      }
    }
    return false;
  }

  private LocalDateTime getDifferenceDateTimeInSeconds(
      LocalDateTime attemptDate, Double penaltyTime) {
    return attemptDate.plus(penaltyTime.longValue(), ChronoUnit.SECONDS);
  }

  private Double getPenaltyTimeRemaining(Double penaltyTime, LocalDateTime attemptLastDate) {
    LocalDateTime now = LocalDateTime.now(ZoneId.of("America/Bogota"));
    Duration duration = Duration.between(attemptLastDate, now);
    Long penaltyTimeSeconds = penaltyTime.longValue();
    Long secondsDifference = duration.getSeconds() - penaltyTimeSeconds;
    return secondsDifference.doubleValue();
  }

  public String getAttemptTimeResult(AttemptTimeResult attemptTimeResult) {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      String attemptTimeResultJson = objectMapper.writeValueAsString(attemptTimeResult);
      return attemptTimeResultJson;
    } catch (JsonProcessingException e) {
      logger.error("Error en getAttemptTimeResult to JSON ", e);
      throw new RuntimeException("Error en getAttemptTimeResult to JSON ");
    }
  }

  public String getLastSuccessfulLoginAttemptDate(String idClient) {
    Optional<LoginAttemptsEntity> loginAttempts =
        loginAttemptsRepository.findByIdClient(UUID.fromString(idClient));
    AttemptEntity attempt;

    if (loginAttempts.isPresent()
        && Objects.nonNull(loginAttempts.get().getSuccessfulAttempt())
        && loginAttempts.get().getSuccessfulAttempt().size() >= 1) {
      attempt =
          loginAttempts
              .get()
              .getSuccessfulAttempt()
              .get(loginAttempts.get().getSuccessfulAttempt().size() - 1);
      return attempt.getAttemptDate().withNano(0).toString();
    } else {
      return "";
    }
  }

  public Boolean isMaxAttemptAllowed(LoginAttemptsEntity loginAttempt) {
    return getFailsAttempts(loginAttempt.getFailsAttempt()).equals(maxAttemptsAllowed - 1);
  }
}
