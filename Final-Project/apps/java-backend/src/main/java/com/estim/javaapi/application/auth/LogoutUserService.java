package com.estim.javaapi.application.auth;

import com.estim.javaapi.domain.user.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Application service responsible for logging out a user.
 *
 * In a typical JWT setup, this will:
 * - Revoke the refresh token so it can't be used to get new access tokens.
 * - Optionally revoke all tokens for the user (if supported).
 *
 * IMPORTANT:
 *  - Logout must be BEST-EFFORT: invalid/expired tokens must NOT cause a 500.
 */
@Service
public class LogoutUserService {

    private static final Logger log = LoggerFactory.getLogger(LogoutUserService.class);

    private final TokenService tokenService;

    public LogoutUserService(TokenService tokenService) {
        this.tokenService = Objects.requireNonNull(tokenService);
    }

    public void logout(LogoutUserCommand command) {
        if (command == null) {
            return;
        }

        String refreshToken = command.refreshToken();
        if (refreshToken != null && !refreshToken.isBlank()) {
            try {
                tokenService.revokeRefreshToken(refreshToken);
            } catch (RuntimeException ex) {
                log.warn("Failed to revoke refresh token during logout (ignored)", ex);
            }
        }

        String accessToken = command.accessToken();
        if (accessToken != null && !accessToken.isBlank()) {
            try {
                UserId userId = tokenService.parseUserIdFromAccessToken(accessToken);
                tokenService.revokeAllForUser(userId);

                log.info("[AUDIT] User logged out: userId={}", userId.value());
            } catch (RuntimeException ex) {
                log.warn("Invalid or expired access token during logout (ignored)", ex);
            }
        }
    }
}
