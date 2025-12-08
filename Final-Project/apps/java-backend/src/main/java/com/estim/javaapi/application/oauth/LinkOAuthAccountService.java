package com.estim.javaapi.application.oauth;

import com.estim.javaapi.domain.common.DomainEventPublisher;
import com.estim.javaapi.domain.user.*;
import com.estim.javaapi.infrastructure.oauth.GoogleOAuthClient;
import com.estim.javaapi.infrastructure.oauth.OAuthUserInfo;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
public class LinkOAuthAccountService {

    private final UserRepository userRepository;
    private final OAuthAccountRepository oAuthAccountRepository;
    private final DomainEventPublisher eventPublisher;
    private final GoogleOAuthClient googleOAuthClient;

    public LinkOAuthAccountService(UserRepository userRepository,
                                   OAuthAccountRepository oAuthAccountRepository,
                                   DomainEventPublisher eventPublisher,
                                   GoogleOAuthClient googleOAuthClient) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.oAuthAccountRepository = Objects.requireNonNull(oAuthAccountRepository);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.googleOAuthClient = Objects.requireNonNull(googleOAuthClient);
    }

    public void link(LinkOAuthAccountCommand command) {
        UserId userId = new UserId(UUID.fromString(command.userId()));
        OAuthProvider provider = OAuthProvider.valueOf(command.provider().toUpperCase());

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String externalUserId;
        String email = null;

        if (provider == OAuthProvider.GOOGLE) {
            // ✅ Wrap Google call → map any runtime error to IllegalArgumentException
            try {
                OAuthUserInfo info = googleOAuthClient.fetchUserInfo(command.externalToken());
                externalUserId = info.externalUserId(); // "sub"
                email = info.email();
            } catch (RuntimeException ex) {
                // invalid / expired token, network error, etc.
                throw new IllegalArgumentException("Failed to validate OAuth token for linking", ex);
            }
        } else {
            // For other providers we still treat externalToken as external ID (for now)
            externalUserId = command.externalToken();
        }

        if (externalUserId == null || externalUserId.isBlank()) {
            throw new IllegalArgumentException("Missing external user id");
        }

        OAuthAccount account = OAuthAccount.create(
            userId,
            provider,
            externalUserId,
            email
        );

        oAuthAccountRepository.save(account);
        user.linkOAuthAccount(account);
        userRepository.save(user);

        eventPublisher.publishAll(user.domainEvents());
        user.clearDomainEvents();
    }
}

