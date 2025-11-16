package com.estim.javaapi.infrastructure.security;

import com.estim.javaapi.application.auth.TokenService;
import com.estim.javaapi.domain.user.UserId;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.hibernate.annotations.MapKeyCompositeType;
import org.springframework.stereotype.Component;

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

    public JwtTokenService(byte[] secretKey,
                           Duration accessTokenTtl,
                           Duration refreshTokenTtl) {

        this.secretKey = Objects.requireNonNull(secretKey);
        this.accessTokenTtl = Objects.requireNonNull(accessTokenTtl);
        this.refreshTokenTtl = Objects.requireNonNull(refreshTokenTtl);
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
        Claims claims = Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .getBody();

        String subject = claims.getSubject();
        return new UserId(UUID.fromString(subject));
    }

    // These revocation methods are still no-op (stateless JWT).
    // You can later implement blacklisting using DB/Redis if needed.

    @Override
    public void revokeRefreshToken(String refreshToken) {
        // no-op for pure stateless JWT; override in a stateful implementation
    }

    @Override
    public void revokeAllForUser(UserId userId) {
        // no-op for pure stateless JWT; override in a stateful implementation
    }
}
