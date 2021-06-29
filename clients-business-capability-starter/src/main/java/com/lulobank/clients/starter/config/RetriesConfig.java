package com.lulobank.clients.starter.config;

import com.lulobank.clients.services.features.RetriesOption;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Configuration
public class RetriesConfig {

    @Value("${identitybiometric.mobile-response.delay-options}")
    private List<Integer> mobileResponseDelays;

    @Value("${identitybiometric.adotech-response.delay-options}")
    private List<Integer> adotechResponseDelays;

    @Value("${risk-engine.delay-options}")
    private List<Integer> riskEngineDelays;

    public Map<Integer, Integer> getHasMap(List<Integer> listDelay) {
        return IntStream.range(0, listDelay.size())
                .boxed()
                .collect(Collectors.toMap(Function.identity(), listDelay::get));
    }

    @Bean
    @Qualifier("adotechResponseRetriesOption")
    public RetriesOption adotechResponseRetriesOption() {
        List<Integer> listDelay=adotechResponseDelays;
        return new RetriesOption(listDelay.size(), getHasMap(listDelay));
    }

    @Bean
    @Qualifier("mobileResponseRetriesOption")
    public RetriesOption mobileResponseRetriesOption() {
        List<Integer> listDelay=mobileResponseDelays;
        return new RetriesOption(listDelay.size(), getHasMap(listDelay));
    }

    @Bean
    @Qualifier("riskEngineRetriesOption")
    public RetriesOption riskEngineRetriesOption() {
        List<Integer> listDelay=riskEngineDelays;
        return new RetriesOption(listDelay.size(), getHasMap(listDelay));
    }

}
