package com.estim.javaapi.application.oauth;

import com.estim.javaapi.application.auth.AuthenticationResult;
import com.estim.javaapi.application.auth.TokenService;
import com.estim.javaapi.domain.common.DomainEventPublisher;
import com.estim.javaapi.domain.user.Email;
import com.estim.javaapi.domain.user.OAuthAccount;
import com.estim.javaapi.domain.user.OAuthAccountId;
import com.estim.javaapi.domain.user.OAuthProvider;
import com.estim.javaapi.domain.user.User;
import com.estim.javaapi.domain.user.UserId;
import com.estim.javaapi.domain.user.UserRepository;
import com.estim.javaapi.domain.user.events.UserLoggedIn;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Application service for logging in a user using an OAuth provider.
 *
 * NOTE: For now, we:
 *  - look up the user by email (as reported by the provider),
 *  - ensure the OAuth account is linked (link it if missing),
 *  - generate tokens for that user.
 *
 * Later you can:
 *  - validate the token server-side via an OAuthProviderClient,
 *  - or search users by provider+externalId instead of email.
 */
@Component
public class LoginWithOAuthService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final DomainEventPublisher eventPublisher;

    public LoginWithOAuthService(UserRepository userRepository,
                                 TokenService tokenService,
                                 DomainEventPublisher eventPublisher) {

        this.userRepository = Objects.requireNonNull(userRepository);
        this.tokenService = Objects.requireNonNull(tokenService);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    public AuthenticationResult login(LoginWithOAuthCommand command) {
        Email email = new Email(command.email());
        OAuthProvider provider = OAuthProvider.valueOf(command.provider().toUpperCase());

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("User not found for OAuth login"));

        // Ensure the OAuth account is linked; if not, link it now
        boolean alreadyLinked = user.linkedAccounts().stream()
            .anyMatch(acc ->
                acc.provider().equals(provider)
                    && acc.externalUserId().equals(command.oauthExternalId())
            );

        if (!alreadyLinked) {
            OAuthAccount newAccount = new OAuthAccount(
                new OAuthAccountId(UUID.randomUUID()),
                provider,
                command.oauthExternalId(),
                email.value(),
                Instant.now()
            );
            user.linkOAuthAccount(newAccount);
            userRepository.save(user);
            eventPublisher.publishAll(user.domainEvents());
            user.clearDomainEvents();
        }

        // Issue tokens for the user
        UserId userId = user.id();
        String accessToken = tokenService.generateAccessToken(userId);
        String refreshToken = tokenService.generateRefreshToken(userId);

        eventPublisher.publish(new UserLoggedIn(userId, Instant.now()));

        return new AuthenticationResult(accessToken, refreshToken, user);
    }
}
