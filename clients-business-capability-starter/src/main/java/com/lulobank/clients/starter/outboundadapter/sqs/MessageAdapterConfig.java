package com.lulobank.clients.starter.outboundadapter.sqs;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.lulobank.clients.services.ports.out.MessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MessageAdapterConfig {

    @Value("${cloud.aws.sqs.blacklist-events}")
    private String awsEndpoint;

    @Value("${cloud.aws.region.static}")
    private String amazonSQSRegion;

    @Value("${cloud.aws.sqs.reporting-events}")
    private String reportingSqsEndpoint;

    @Value("${cloud.aws.sqs.saving-account-events}")
    private String savingsSqsEndpoint;

    @Value("${cloud.aws.sqs.credits-events}")
    private String creditsSqsEndpoint;

    @Value("${cloud.aws.sqs.client-alerts-events}")
    private String sqsNotificationEndPoint;

    @Value("${cloud.aws.sqs.cards-events}")
    private String cardsSqsEndpoint;

    @Bean
    public MessageService notificationService() {
        DigitalEvidenceEvent digitalEvidenceEvent = new DigitalEvidenceEvent(reportingSqsEndpoint);
        UpdateClientAddressNotificationEvent updateClientAddressNotificationEvent = new UpdateClientAddressNotificationEvent(sqsNotificationEndPoint);
        NotificationDisabledEvent notificationDisabledEvent = new NotificationDisabledEvent(sqsNotificationEndPoint);
        return new QueueServiceAdapter(getQueueMessagingTemplate(), digitalEvidenceEvent,
                getUpdateEmailEventList(),updateClientAddressNotificationEvent,
                notificationDisabledEvent);
    }

    private List<UpdateProductEmailEvent> getUpdateEmailEventList(){
        return Stream.of(new UpdateProductEmailEvent(savingsSqsEndpoint),
                new UpdateProductEmailEvent(creditsSqsEndpoint),
                new UpdateProductEmailEvent(cardsSqsEndpoint))
                .collect(Collectors.toList());
    }

    public QueueMessagingTemplate getQueueMessagingTemplate() {
        return new QueueMessagingTemplate(amazonSQSAsync());
    }

    public AmazonSQSAsync amazonSQSAsync() {
        return AmazonSQSAsyncClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(awsEndpoint, amazonSQSRegion))
                .build();
    }

}