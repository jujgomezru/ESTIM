package com.estim.javaapi.application.oauth;

import com.estim.javaapi.application.auth.AuthenticationResult;
import com.estim.javaapi.application.auth.TokenService;
import com.estim.javaapi.domain.common.DomainEventPublisher;
import com.estim.javaapi.domain.user.*;
import com.estim.javaapi.domain.user.events.UserLoggedIn;
import com.estim.javaapi.infrastructure.oauth.GoogleOAuthClient;
import com.estim.javaapi.infrastructure.oauth.OAuthUserInfo;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Objects;

@Component
public class LoginWithOAuthService {

    private final OAuthAccountRepository oAuthAccountRepository;
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final DomainEventPublisher eventPublisher;
    private final GoogleOAuthClient googleOAuthClient;

    public LoginWithOAuthService(OAuthAccountRepository oAuthAccountRepository,
                                 UserRepository userRepository,
                                 TokenService tokenService,
                                 DomainEventPublisher eventPublisher,
                                 GoogleOAuthClient googleOAuthClient) {
        this.oAuthAccountRepository = Objects.requireNonNull(oAuthAccountRepository);
        this.userRepository = Objects.requireNonNull(userRepository);
        this.tokenService = Objects.requireNonNull(tokenService);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.googleOAuthClient = Objects.requireNonNull(googleOAuthClient);
    }

    public AuthenticationResult login(LoginWithOAuthCommand command) {
        OAuthProvider provider = OAuthProvider.valueOf(command.provider().toUpperCase());

        String externalId;

        if (provider == OAuthProvider.GOOGLE) {
            // ✅ Wrap Google call → map to IllegalArgumentException
            try {
                OAuthUserInfo info = googleOAuthClient.fetchUserInfo(command.token());
                externalId = info.externalUserId();
            } catch (RuntimeException ex) {
                throw new IllegalArgumentException("Failed to validate OAuth token for login", ex);
            }
        } else {
            externalId = command.oauthExternalId();
        }

        if (externalId == null || externalId.isBlank()) {
            throw new IllegalArgumentException("Missing OAuth external id");
        }

        OAuthAccount account = oAuthAccountRepository
            .findByProviderAndExternalUserId(provider, externalId)
            .orElseThrow(() -> new IllegalArgumentException("OAuth account not linked"));

        User user = userRepository.findById(account.userId())
            .orElseThrow(() -> new IllegalStateException("User not found for linked OAuth account"));

        user.markLogin();
        userRepository.save(user);

        UserId userId = user.id();
        String accessToken = tokenService.generateAccessToken(userId);
        String refreshToken = tokenService.generateRefreshToken(userId);

        eventPublisher.publish(new UserLoggedIn(userId, Instant.now()));

        return new AuthenticationResult(accessToken, refreshToken, user);
    }
}

