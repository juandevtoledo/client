package com.lulobank.clients.starter.outboundadapter.dynamo;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DynamoDBLocal {

  @Value("${cloud.aws.dynamodb.endpoint}")
  private String amazonDynamoDBEndpoint;

  @Value("${cloud.aws.region.static}")
  private String amazonRegion;

  @Bean
  public AmazonDynamoDB amazonDynamoDB() {
    return AmazonDynamoDBClientBuilder.standard()
        .withEndpointConfiguration(
            new AwsClientBuilder.EndpointConfiguration(amazonDynamoDBEndpoint, amazonRegion))
        .build();
  }

  @Bean
  public DynamoDB dynamoDB() {
    return new DynamoDB(amazonDynamoDB());
  }

}
