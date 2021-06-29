package com.lulobank.clients.starter.v3.adapters.out.dynamo.dto;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.lulobank.clients.services.utils.LoginAttemptsService;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@DynamoDBTable(tableName = LoginAttemptsService.LOGIN_ATTEMPTS_TABLE)
public class LoginAttemptsEntity {

  @DynamoDBHashKey private UUID idClient;
  private List<AttemptEntity> successfulAttempt;
  private List<AttemptEntity> failsAttempt;
}
