package com.estim.javaapi.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(CorsConfigurationSource corsConfigurationSource,
                          JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.corsConfigurationSource = corsConfigurationSource;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Stateless JSON API
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .cors(cors -> cors.configurationSource(corsConfigurationSource))

            .authorizeHttpRequests(auth -> auth
                // Allow ALL preflight requests
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Public endpoints for auth
                .requestMatchers("/auth/register", "/auth/login").permitAll()

                // Everything under /me/** now requires a valid JWT
                .requestMatchers("/me/**").authenticated()

                // Other endpoints, decide as you wish; for now require auth
                .anyRequest().authenticated()
            )

            // 🔹 Plug our JWT filter before Spring's own auth filter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
