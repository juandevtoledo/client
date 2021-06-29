package com.lulobank.clients.starter.adapter.out.reporting.config;

import com.lulobank.clients.services.application.port.out.reporting.ReportingPort;
import com.lulobank.clients.starter.adapter.out.reporting.ReportingAdapter;
import com.lulobank.clients.starter.adapter.out.reporting.ReportingClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReportingServiceConfig {

    @Value("${services.reporting.url}")
    private String serviceDomain;

    @Bean
    public ReportingClient reportingClient() {
        return new ReportingClient(serviceDomain);
    }

    @Bean
    public ReportingPort getReportingPort(ReportingClient reportingClient) {
        return new ReportingAdapter(reportingClient);
    }
}
