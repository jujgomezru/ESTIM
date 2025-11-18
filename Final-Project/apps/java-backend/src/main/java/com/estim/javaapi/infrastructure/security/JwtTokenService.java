package com.estim.javaapi.infrastructure.security;

import com.estim.javaapi.application.auth.TokenService;
import com.estim.javaapi.domain.user.UserId;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * JWT-based implementation of TokenService using HS256.
 *
 * Requires dependencies like:
 *   implementation "io.jsonwebtoken:jjwt-api:0.12.5"
 *   runtimeOnly "io.jsonwebtoken:jjwt-impl:0.12.5"
 *   runtimeOnly "io.jsonwebtoken:jjwt-jackson:0.12.5"
 */
@Component
public class JwtTokenService implements TokenService {

    private final byte[] secretKey;
    private final Duration accessTokenTtl;
    private final Duration refreshTokenTtl;

    public JwtTokenService(
        @Value("${security.jwt.secret:dev-secret-change-me}") String secret,
        @Value("${security.jwt.access-token-ttl:PT15M}") Duration accessTokenTtl,
        @Value("${security.jwt.refresh-token-ttl:P7D}") Duration refreshTokenTtl
    ) {
        this.secretKey = Objects.requireNonNull(secret, "secret must not be null")
            .getBytes(StandardCharsets.UTF_8);
        this.accessTokenTtl = Objects.requireNonNull(accessTokenTtl, "accessTokenTtl must not be null");
        this.refreshTokenTtl = Objects.requireNonNull(refreshTokenTtl, "refreshTokenTtl must not be null");
    }

    @Override
    public String generateAccessToken(UserId userId) {
        return generateToken(userId, accessTokenTtl, "access");
    }

    @Override
    public String generateRefreshToken(UserId userId) {
        return generateToken(userId, refreshTokenTtl, "refresh");
    }

    private String generateToken(UserId userId, Duration ttl, String type) {
        Instant now = Instant.now();
        Instant expiry = now.plus(ttl);

        return Jwts.builder()
            .setSubject(userId.value().toString())
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(expiry))
            .claim("typ", type)
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
    }

    @Override
    public UserId parseUserIdFromAccessToken(String token) {
        try {
            Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();

            String subject = claims.getSubject();
            return new UserId(UUID.fromString(subject));
        } catch (JwtException | IllegalArgumentException ex) {
            // Includes ExpiredJwtException, malformed token, bad signature, etc.
            throw new IllegalArgumentException("Invalid or expired access token", ex);
        }
    }

    @Override
    public void revokeRefreshToken(String refreshToken) {
        // no-op for pure stateless JWT; override in a stateful implementation
    }

    @Override
    public void revokeAllForUser(UserId userId) {
        // no-op for pure stateless JWT; override in a stateful implementation
    }
}
