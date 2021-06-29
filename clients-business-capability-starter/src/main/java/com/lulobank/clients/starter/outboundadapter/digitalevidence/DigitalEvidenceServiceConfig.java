package com.lulobank.clients.starter.outboundadapter.digitalevidence;

import co.com.lulobank.tracing.restTemplate.RestTemplateClient;
import com.lulobank.clients.services.application.port.out.reporting.ReportingPort;
import com.lulobank.clients.services.ports.out.AcceptancesDocumentService;
import com.lulobank.clients.services.ports.out.DigitalEvidenceService;
import com.lulobank.clients.starter.adapter.out.reporting.config.ReportingServiceConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ReportingServiceConfig.class)
public class DigitalEvidenceServiceConfig {

    @Bean
    public DigitalEvidenceService getDigitalEvidenceService(ReportingPort reportingPort) {
        return new DigitalEvidenceServiceAdapter(reportingPort);
    }

    @Bean
    public AcceptancesDocumentService getAcceptancesDocumentService(@Qualifier("reportingRestTemplate") RestTemplateClient savingsRestTemplate) {
        return new AcceptancesDocumentServiceAdapter(savingsRestTemplate);
    }

}
