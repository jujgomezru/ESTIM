package com.estim.javaapi.application.oauth;

import com.estim.javaapi.application.auth.AuthenticationResult;
import com.estim.javaapi.application.auth.PasswordHasher;
import com.estim.javaapi.application.auth.TokenService;
import com.estim.javaapi.domain.common.DomainEventPublisher;
import com.estim.javaapi.domain.user.*;
import com.estim.javaapi.infrastructure.oauth.GoogleOAuthClient;
import com.estim.javaapi.infrastructure.oauth.OAuthUserInfo;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
public class RegisterWithOAuthService {

    private final UserRepository userRepository;
    private final OAuthAccountRepository oAuthAccountRepository;
    private final PasswordHasher passwordHasher;
    private final TokenService tokenService;
    private final DomainEventPublisher eventPublisher;
    private final GoogleOAuthClient googleOAuthClient;

    public RegisterWithOAuthService(UserRepository userRepository,
                                    OAuthAccountRepository oAuthAccountRepository,
                                    PasswordHasher passwordHasher,
                                    TokenService tokenService,
                                    DomainEventPublisher eventPublisher,
                                    GoogleOAuthClient googleOAuthClient) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.oAuthAccountRepository = Objects.requireNonNull(oAuthAccountRepository);
        this.passwordHasher = Objects.requireNonNull(passwordHasher);
        this.tokenService = Objects.requireNonNull(tokenService);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.googleOAuthClient = Objects.requireNonNull(googleOAuthClient);
    }

    public AuthenticationResult register(RegisterWithOAuthCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Command is required");
        }

        OAuthProvider provider = OAuthProvider.valueOf(command.provider().toUpperCase());

        if (provider != OAuthProvider.GOOGLE) {
            throw new IllegalArgumentException("OAuth provider not supported for registration yet: " + provider);
        }

        // 1) Fetch info from Google using access token
        OAuthUserInfo info = googleOAuthClient.fetchUserInfo(command.accessToken());
        String externalUserId = info.externalUserId();
        String emailStr = info.email();
        String displayNameFromGoogle = info.displayName();

        if (externalUserId == null || externalUserId.isBlank()) {
            throw new IllegalArgumentException("OAuth provider did not return a stable user id (sub)");
        }
        if (emailStr == null || emailStr.isBlank()) {
            throw new IllegalArgumentException("OAuth provider did not return an email");
        }

        Email email = Email.of(emailStr);

        // 2) If an OAuthAccount already exists for this provider+externalId → treat as login
        var existingAccountOpt =
            oAuthAccountRepository.findByProviderAndExternalUserId(provider, externalUserId);

        if (existingAccountOpt.isPresent()) {
            OAuthAccount existingAccount = existingAccountOpt.get();
            User user = userRepository.findById(existingAccount.userId())
                .orElseThrow(() -> new IllegalStateException("User not found for existing OAuth account"));

            // Mark login & issue tokens
            user.markLogin();
            userRepository.save(user);

            String accessToken = tokenService.generateAccessToken(user.id());
            String refreshToken = tokenService.generateRefreshToken(user.id());

            eventPublisher.publish(new com.estim.javaapi.domain.user.events.UserLoggedIn(
                user.id(),
                java.time.Instant.now()
            ));

            return new AuthenticationResult(accessToken, refreshToken, user);
        }

        // 3) If a *local* user already exists with this email → for now, reject.
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                "Email already in use. Please log in with your email and password."
            );
        }

        // 4) Generate a displayName
        String displayName = generateDisplayName(displayNameFromGoogle, emailStr);

        // If there is a collision → you can either:
        //  - try another variant, or
        //  - fail. For now, let's fail with a clear message.
        if (userRepository.existsByDisplayName(displayName)) {
            throw new IllegalArgumentException("Display name already in use: " + displayName);
        }

        // 5) We still need a password hash in the domain → generate a random one
        String randomPassword = "oauth-" + UUID.randomUUID();
        PasswordHash passwordHash = passwordHasher.hash(randomPassword);

        UserId userId = new UserId(UUID.randomUUID());

        PrivacySettings privacySettings = new PrivacySettings(
            true,
            true,
            true
        );

        UserProfile profile = new UserProfile(
            displayName,
            null,
            privacySettings
        );

        // Reuse your domain factory
        User user = User.register(
            userId,
            email,
            passwordHash,
            profile
        );

        // 6) Create OAuthAccount and link
        OAuthAccount account = OAuthAccount.create(
            userId,
            provider,
            externalUserId,
            emailStr
        );

        user.linkOAuthAccount(account);

        // Persist both
        userRepository.save(user);
        oAuthAccountRepository.save(account);

        // Publish domain events
        eventPublisher.publishAll(user.domainEvents());
        user.clearDomainEvents();

        // 7) Issue tokens as in regular login
        String accessToken = tokenService.generateAccessToken(user.id());
        String refreshToken = tokenService.generateRefreshToken(user.id());

        return new AuthenticationResult(accessToken, refreshToken, user);
    }

    private String generateDisplayName(String displayNameFromGoogle, String emailStr) {
        String base;
        if (displayNameFromGoogle != null && !displayNameFromGoogle.isBlank()) {
            base = displayNameFromGoogle;
        } else {
            // fallback: local-part of email
            int at = emailStr.indexOf('@');
            base = at > 0 ? emailStr.substring(0, at) : emailStr;
        }

        // Replace spaces and invalid chars with underscore
        String candidate = base
            .trim()
            .replaceAll("\\s+", "_")
            .replaceAll("[^A-Za-z0-9_]", "_");

        // Ensure it matches ^[A-Za-z_][A-Za-z0-9_]*$
        if (!candidate.matches("^[A-Za-z_].*")) {
            candidate = "u_" + candidate;
        }
        // If everything got wiped somehow, hard fallback:
        if (!candidate.matches("^[A-Za-z_][A-Za-z0-9_]*$")) {
            candidate = "user_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        }

        return candidate;
    }
}
