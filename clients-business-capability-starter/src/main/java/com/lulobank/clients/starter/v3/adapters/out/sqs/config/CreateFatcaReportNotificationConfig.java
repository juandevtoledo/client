package com.lulobank.clients.starter.v3.adapters.out.sqs.config;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.clients.starter.v3.adapters.out.sqs.CreateFatcaReportNotificationAdapter;
import com.lulobank.clients.v3.adapters.port.out.notification.report.CreateReportNotification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CreateFatcaReportNotificationConfig {


    @Value("${cloud.aws.sqs.reporting-rx-events}")
    private String reportingXbcSqsEndpoint;

   @Bean
   public CreateReportNotification getCreateFatcaReportNotification(SqsBraveTemplate sqsBraveTemplate){
       return new CreateFatcaReportNotificationAdapter(reportingXbcSqsEndpoint,sqsBraveTemplate);
   }

}
