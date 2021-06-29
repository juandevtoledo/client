package com.lulobank.clients.starter.outboundadapter.dynamo;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.ListTablesRequest;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

@Component
public class DbHealthCheck implements HealthIndicator {
  private static final Logger LOGGER = LoggerFactory.getLogger(DbHealthCheck.class);
  private static final String CUSTO_DB = "database";
  private static final String NAME_DB = "DynamoDB";
  private static final String STATUS_DB = "database.status";
  private static final String SCHEMA_DB = "database.schema";

  @Autowired private AmazonDynamoDB amazonDynamoDB;

  @Override
  public Health health() {

    int errorCode = checkDynamoDB(); // perform some specific health check
    if (errorCode != 0) {
      return Health.up()
          .withDetail(CUSTO_DB, NAME_DB)
          .withDetail(STATUS_DB, Status.DOWN.getCode())
          .build();
    }

    errorCode = checkDynamoDBSchema();
    if (errorCode != 0) {
      return Health.up()
          .withDetail(CUSTO_DB, NAME_DB)
          .withDetail(STATUS_DB, Status.UP.getCode())
          .withDetail(CUSTO_DB, NAME_DB)
          .withDetail(SCHEMA_DB, Status.DOWN.getCode())
          .build();
    }
    return Health.up()
        .withDetail(CUSTO_DB, NAME_DB)
        .withDetail(STATUS_DB, Status.UP.getCode())
        .withDetail(CUSTO_DB, NAME_DB)
        .withDetail(SCHEMA_DB, Status.UP.getCode())
        .build();
  }

  private int checkDynamoDB() {
    try {
      ListTablesRequest request = new ListTablesRequest();
      request.setSdkClientExecutionTimeout(5000);
      amazonDynamoDB.listTables(request);
      return 0; // To verify that table Clients exist
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      return -1;
    }
  }

  private int checkDynamoDBSchema() {
    try {
      ListTablesRequest request = new ListTablesRequest();
      ListTablesResult response = amazonDynamoDB.listTables(request);
      if (response.getTableNames().stream().anyMatch(x -> x.equals("Clients"))) {
        return 0; // To verify that table Clients exist
      }
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    }
    return -1;
  }
}
