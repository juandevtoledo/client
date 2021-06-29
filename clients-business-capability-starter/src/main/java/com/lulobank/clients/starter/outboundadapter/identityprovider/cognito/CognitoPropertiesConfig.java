package com.lulobank.clients.starter.outboundadapter.identityprovider.cognito;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.lulobank.clients.services.utils.CognitoProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CognitoPropertiesConfig {

  @Value("${cloud.aws.cognito.pool-id}")
  private String poolId;

  @Value("${cloud.aws.cognito.clientapp-id}")
  private String clientAppId;

  @Value("${cloud.aws.cognito.custom-domain}")
  private String customDomain;

  @Value("${cloud.aws.cognito.region}")
  private String region;

  @Value("${cloud.aws.cognito.endpoint}")
  private String amazonCognitoEndpoint;

  @Bean
  public CognitoProperties cognitoProperties() {
    AWSCognitoIdentityProvider cognitoIdentityProvider =
        AWSCognitoIdentityProviderClientBuilder.standard()
            .withEndpointConfiguration(
                new AwsClientBuilder.EndpointConfiguration(amazonCognitoEndpoint, region))
            .build();
    return new CognitoProperties(poolId, clientAppId, customDomain, cognitoIdentityProvider);
  }
}
