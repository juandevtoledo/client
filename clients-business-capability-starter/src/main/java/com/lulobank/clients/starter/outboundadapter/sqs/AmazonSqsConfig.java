package com.lulobank.clients.starter.outboundadapter.sqs;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.lulobank.clients.services.actions.MessageToSQSCheckBiometricIdentity;
import com.lulobank.clients.services.features.onboardingclients.action.MessageToNotifySQSClients;
import com.lulobank.clients.services.features.onboardingclients.action.MessageToNotifySQSEconomicInformation;
import com.lulobank.clients.services.features.onboardingclients.action.MessageToNotifySQSRiskEngine;
import com.lulobank.clients.services.features.onboardingclients.action.MessageToNotifySQSRiskEngineResponse;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class AmazonSqsConfig {

  @Value("${cloud.aws.region.static}")
  private String amazonSqsRegion;

  @Value("${cloud.aws.sqs.blacklist-events}")
  private String awsEndpoint;

  @Value("${cloud.aws.sqs.riskengine-events}")
  private String sqsRiskEngineEndPoint;

  @Value("${cloud.aws.sqs.client-events}")
  private String sqsClientsEndPoint;

  @Value("${cloud.aws.sqs.client-riskresponse-events}")
  private String sqsRiskEngineResponseEndPoint;


  public AmazonSQSAsync amazonSQSAsync() {
    return AmazonSQSAsyncClientBuilder.standard()
        .withEndpointConfiguration(
            new AwsClientBuilder.EndpointConfiguration(awsEndpoint, amazonSqsRegion))
        .build();
  }


  @Bean
  public MessageToNotifySQSRiskEngine messageToNotifySQSRiskEngine() {
    return new MessageToNotifySQSRiskEngine(new QueueMessagingTemplate(amazonSQSAsync()), sqsRiskEngineEndPoint);
  }

  @Bean
  public MessageToNotifySQSClients messageToNotifySQSClients() {
    return new MessageToNotifySQSClients(new QueueMessagingTemplate(amazonSQSAsync()), sqsClientsEndPoint);
  }

  @Bean
  public MessageToNotifySQSRiskEngineResponse messageToNotifySQSRiskEngineResponse() {
    return new MessageToNotifySQSRiskEngineResponse(
            new QueueMessagingTemplate(amazonSQSAsync()), sqsRiskEngineResponseEndPoint);
  }

  @Bean
  public MessageToSQSCheckBiometricIdentity messageToSQSCheckBiometricIdentity() {
    return new MessageToSQSCheckBiometricIdentity(new QueueMessagingTemplate(amazonSQSAsync()), sqsClientsEndPoint);
  }

  @Bean
  public MessageToNotifySQSEconomicInformation messageToNotifySQSEconomicInformation() {
    return new MessageToNotifySQSEconomicInformation(new QueueMessagingTemplate(amazonSQSAsync()), sqsRiskEngineEndPoint);
  }
}
