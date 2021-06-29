package com.lulobank.clients.starter.v3.adapters.config;

import co.com.lulobank.tracing.restTemplate.RestTemplateClient;
import com.lulobank.clients.starter.adapter.out.digitalevidence.DigitalEvidenceAdapter;
import com.lulobank.clients.v3.adapters.port.out.digitalevidence.DigitalEvidenceServicePort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({RestTemplateClientConfig.class})
public class DigitalEvidenceConfig {

    @Bean
    public DigitalEvidenceServicePort geDigitalEvidenceServicePort(@Qualifier("digitalEvidenceRestTemplate") RestTemplateClient digitalEvidenceRestTemplate) {
        return new DigitalEvidenceAdapter(digitalEvidenceRestTemplate);
    }
}
