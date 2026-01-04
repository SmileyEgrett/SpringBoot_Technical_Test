package com.example.fileproc.config;

import com.example.fileproc.domain.IpBlockingPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IpBlockingConfig {

    @Bean
    public IpBlockingPolicy ipBlockingPolicy(IpBlockingProperties properties) {
        return new IpBlockingPolicy(properties.getBlockedCountries(), properties.getBlockedIsps());
    }
}
