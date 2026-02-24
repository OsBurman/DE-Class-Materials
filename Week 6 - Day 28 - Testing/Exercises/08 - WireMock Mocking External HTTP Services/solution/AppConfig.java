package com.testing;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Configures a RestTemplate with a short read timeout so the timeout test
 * can trigger within a reasonable test duration.
 */
@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .connectTimeout(Duration.ofMillis(1000))
                .readTimeout(Duration.ofMillis(1000))   // 1-second read timeout
                .build();
    }
}
