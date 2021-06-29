package com.lulobank.clients.starter.outboundadapter.profile;

import co.com.lulobank.tracing.restTemplate.RestTemplateClient;
import com.lulobank.clients.services.ports.out.ProfileService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProfileServiceAdapterConfig {

    @Bean
    public ProfileService buildProfileService(@Qualifier("profileRestTemplate") RestTemplateClient profileRestTemplate){
        return new ProfileServiceAdapter(profileRestTemplate);
    }
}
