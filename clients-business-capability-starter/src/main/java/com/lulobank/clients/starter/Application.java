package com.lulobank.clients.starter;

import brave.sampler.Sampler;
import co.com.lulobank.tracing.error.tracking.SentryConfigurationRunner;
import co.com.lulobank.tracing.sqs.EventProcessorConfig;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.clients.starter.config.AttemptsConfig;
import com.lulobank.clients.starter.v3.adapters.out.dynamo.ClientsDataRepository;
import com.lulobank.events.impl.SqsConfig;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.Locale;
import java.util.TimeZone;

@EnableCaching
@SpringBootApplication
@EntityScan(basePackages = "com.lulobank.clients.services")
@ServletComponentScan
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan(
        basePackages = {
                "com.lulobank.clients.starter.config",
                "com.lulobank.clients.starter.adapter.in",
                "com.lulobank.clients.starter.adapter.config",
                "com.lulobank.clients.services",
                "com.lulobank.clients.starter.outboundadapter",
                "com.lulobank.clients.starter.v3.adapters.out.dynamo",
                "com.lulobank.clients.starter.inboundadapter",
                "com.lulobank.biometric",
                "com.lulobank.clients.starter.v3.adapters.out.savingsaccountservice",
                "com.lulobank.clients.starter.v3.adapters.out.firebase",
                "com.lulobank.clients.starter.v3.adapters.out.sqs",
                "com.lulobank.clients.starter.v3.adapters.in",
                "com.lulobank.clients.starter.v3.adapters.config",
                "com.lulobank.clients.starter.v3.handler",
                "com.lulobank.logger",
                "co.com.lulobank"
        },
        basePackageClasses = {SentryConfigurationRunner.class, EventProcessorConfig.class, SqsConfig.class})
@EnableConfigurationProperties(AttemptsConfig.class)
@EnableDynamoDBRepositories(basePackageClasses = {ClientsRepository.class, ClientsDataRepository.class})
public class Application {

    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
        TimeZone.setDefault(TimeZone.getTimeZone("America/Bogota"));
        Locale.setDefault(Locale.forLanguageTag("es-CO"));
    }

    @Bean
    public Sampler defaultSampler() {
        return Sampler.ALWAYS_SAMPLE;
    }


}
