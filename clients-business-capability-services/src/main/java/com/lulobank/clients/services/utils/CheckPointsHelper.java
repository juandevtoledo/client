package com.lulobank.clients.services.utils;

import com.lulobank.clients.sdk.operations.util.CheckPoints;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class CheckPointsHelper {

  private CheckPointsHelper() {}

  public static final Predicate<ClientEntity> isCheckpointPresent =
      clientEntity ->
          Objects.nonNull(clientEntity.getOnBoardingStatus())
              && Objects.nonNull(clientEntity.getOnBoardingStatus().getCheckpoint());

  public static final Consumer<ClientEntity> setCheckpointSavingAccountCreated =
      clientEntity -> {
        if (isCheckpointPresent.test(clientEntity))
          clientEntity
              .getOnBoardingStatus()
              .setCheckpoint(CheckPoints.SAVING_ACCOUNT_CREATED.name());
      };
  public static final Consumer<ClientEntity> setCheckpointClientVerification =
      clientEntity -> {
        if (isCheckpointPresent.test(clientEntity))
          clientEntity.getOnBoardingStatus().setCheckpoint(CheckPoints.CLIENT_VERIFICATION.name());
      };

  public static final Consumer<ClientEntity> setCheckpointBlacklisted =
      clientEntity -> {
        if (isCheckpointPresent.test(clientEntity))
          clientEntity.getOnBoardingStatus().setCheckpoint(CheckPoints.BLACKLISTED.name());
      };

  public static final Function<ClientEntity, CheckPoints> getCheckPointFromClientEntity =
      clientEntity -> {
        if (isCheckpointPresent.test(clientEntity)) {
          return CheckPoints.valueOf(clientEntity.getOnBoardingStatus().getCheckpoint());
        }
        return CheckPoints.NONE;
      };

  public static final Predicate<CheckPoints> isClientVerificationCheckPoint =
      checkPoints -> CheckPoints.CLIENT_VERIFICATION.equals(checkPoints);

  public static final Predicate<CheckPoints> isNoneCheckPoint =
      checkPoints -> CheckPoints.NONE.equals(checkPoints);

  public static final Predicate<ClientEntity> isClientVerificationStatus =
      clientEntity ->
          isClientVerificationCheckPoint.test(
              CheckPoints.valueOf(clientEntity.getOnBoardingStatus().getCheckpoint()));

  public static final Function<ClientEntity, CheckPoints> getGetCheckPointFromClientEntity =
      clientEntity -> {
        CheckPoints checkPointsClientEntity = CheckPoints.NONE;
        if (isCheckpointPresent.test(clientEntity)) {
          checkPointsClientEntity =
              CheckPoints.valueOf(clientEntity.getOnBoardingStatus().getCheckpoint());
        }
        return checkPointsClientEntity;
      };
}
