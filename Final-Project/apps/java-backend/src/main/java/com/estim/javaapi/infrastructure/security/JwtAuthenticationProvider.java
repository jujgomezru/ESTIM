package com.estim.javaapi.infrastructure.security;

import com.estim.javaapi.application.auth.TokenService;
import com.estim.javaapi.domain.user.UserId;
import io.jsonwebtoken.JwtException;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Authentication provider that uses a JWT access token to authenticate a user
 * and populate the SecurityContext.
 *
 * An HTTP filter / interceptor should call authenticateFromAuthorizationHeader(...)
 * at the beginning of a request.
 */
@Component
public class JwtAuthenticationProvider {

    private final TokenService tokenService;

    public JwtAuthenticationProvider(TokenService tokenService) {
        this.tokenService = Objects.requireNonNull(tokenService);
    }

    /**
     * Parses the Authorization header, extracts the Bearer token, and populates the SecurityContext.
     *
     * @param authorizationHeader e.g. "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
     * @throws IllegalArgumentException if the header is missing/invalid or the token cannot be parsed.
     */
    public void authenticateFromAuthorizationHeader(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new IllegalArgumentException("Missing Authorization header");
        }

        String prefix = "Bearer ";
        if (!authorizationHeader.startsWith(prefix)) {
            throw new IllegalArgumentException("Invalid Authorization header format");
        }

        String token = authorizationHeader.substring(prefix.length()).trim();
        if (token.isEmpty()) {
            throw new IllegalArgumentException("Empty Bearer token");
        }

        try {
            // This may throw JwtException or IllegalArgumentException if the token is invalid/malformed
            UserId userId = tokenService.parseUserIdFromAccessToken(token);

            AuthenticatedUser authenticatedUser = new AuthenticatedUser(userId);
            SecurityContext.setCurrentUser(authenticatedUser);

        } catch (JwtException | IllegalArgumentException ex) {
            // Normalize any JWT-related parsing/validation errors
            throw new IllegalArgumentException("Invalid or malformed access token", ex);
        }
    }

    /**
     * Clears the current authentication from the SecurityContext.
     * Should be called at the end of request processing.
     */
    public void clearAuthentication() {
        SecurityContext.clear();
    }
}
