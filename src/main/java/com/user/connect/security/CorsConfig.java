package com.user.connect.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
public class CorsConfig {

    @Value("#{'${spring.graphql.cors.allowed-origins}'.split(',')}")
    private List<String> allowedOrigins;

    @Value("#{'${spring.graphql.cors.allowed-methods}'.split(',')}")
    private List<String> allowedMethods;

    @Value("#{'${spring.graphql.cors.allowed-headers}'.split(',')}")
    private List<String> allowedHeaders;

    @Value("#{'${spring.graphql.cors.exposed-headers}'.split(',')}")
    private List<String> expectedHeaders;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(allowedOrigins);
        corsConfiguration.setAllowedHeaders(allowedHeaders);
        corsConfiguration.setAllowedMethods(allowedMethods);
        corsConfiguration.setExposedHeaders(expectedHeaders);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("api/**", corsConfiguration);
        return source;
    }
}
