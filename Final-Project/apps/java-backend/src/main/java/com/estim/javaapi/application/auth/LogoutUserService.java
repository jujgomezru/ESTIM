package com.estim.javaapi.application.auth;

import com.estim.javaapi.domain.user.UserId;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Application service responsible for logging out a user.
 *
 * In a typical JWT setup, this will:
 * - Revoke the refresh token so it can't be used to get new access tokens.
 * - Optionally revoke all tokens for the user (if supported).
 *
 * Actual revocation strategy is implemented in the TokenService (e.g. DB, cache, blacklist).
 */
@Service
public class LogoutUserService {

    private final TokenService tokenService;

    public LogoutUserService(TokenService tokenService) {
        this.tokenService = Objects.requireNonNull(tokenService);
    }

    public void logout(LogoutUserCommand command) {
        // Revoke the provided refresh token, if any
        if (command.refreshToken() != null && !command.refreshToken().isBlank()) {
            tokenService.revokeRefreshToken(command.refreshToken());
        }

        // Optionally, revoke all tokens for this user (if implementation supports it)
        if (command.accessToken() != null && !command.accessToken().isBlank()) {
            UserId userId = tokenService.parseUserIdFromAccessToken(command.accessToken());
            tokenService.revokeAllForUser(userId);
        }

        // No domain events here by default. If you want one (UserLoggedOut), we can add it later.
    }
}
