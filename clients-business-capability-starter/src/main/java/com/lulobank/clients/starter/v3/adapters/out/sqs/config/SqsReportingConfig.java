package com.lulobank.clients.starter.v3.adapters.out.sqs.config;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.clients.services.application.port.out.reporting.ReportingMessagingPort;
import com.lulobank.clients.starter.v3.adapters.out.sqs.SqsReportingAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SqsReportingConfig {

    @Value("${cloud.aws.sqs.reporting-events}")
    private String sqsReporting;

    @Bean
    public ReportingMessagingPort getReportingMessagingPort(SqsBraveTemplate sqsBraveTemplate) {
        return new SqsReportingAdapter(sqsReporting, sqsBraveTemplate);
    }
}
