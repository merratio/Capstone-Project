package com.mp.capstone.project.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Spring configuration that exposes {@link RestTemplate} and {@link ObjectMapper} beans.
 *
 * <p>{@code RestTemplate} is not auto-configured by Spring Boot so it must be
 * declared explicitly. {@code ObjectMapper} is used by {@link com.mp.capstone.project.service.Auth0ManagementService}
 * to safely deserialize Auth0 API responses into typed structures without raw casts.
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}