package com.estim.javaapi.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(CorsConfigurationSource corsConfigurationSource) {
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // We’re a stateless JSON API, CSRF tokens are not needed
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Use the CorsConfigurationSource from CorsConfig
            .cors(cors -> cors.configurationSource(corsConfigurationSource))

            .authorizeHttpRequests(auth -> auth
                // Allow ALL preflight requests
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Public endpoints for auth
                .requestMatchers("/auth/register", "/auth/login").permitAll()

                // Everything else must be authenticated
                .anyRequest().authenticated()
            );

        // TODO: if later you add a JWT filter, you plug it here with http.addFilterBefore(...)

        return http.build();
    }
}
