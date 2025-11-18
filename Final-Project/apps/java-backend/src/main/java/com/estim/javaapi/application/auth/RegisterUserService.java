package com.estim.javaapi.application.auth;

import com.estim.javaapi.domain.common.DomainEventPublisher;
import com.estim.javaapi.domain.user.Email;
import com.estim.javaapi.domain.user.PrivacySettings;
import com.estim.javaapi.domain.user.User;
import com.estim.javaapi.domain.user.UserId;
import com.estim.javaapi.domain.user.UserProfile;
import com.estim.javaapi.domain.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

/**
 * Application service for registering a new user.
 */
@Service
public class RegisterUserService {

    private final UserRepository userRepository;
    private final PasswordPolicy passwordPolicy;
    private final PasswordHasher passwordHasher;
    private final DomainEventPublisher eventPublisher;

    public RegisterUserService(UserRepository userRepository,
                               PasswordPolicy passwordPolicy,
                               PasswordHasher passwordHasher,
                               DomainEventPublisher eventPublisher) {

        this.userRepository = Objects.requireNonNull(userRepository);
        this.passwordPolicy = Objects.requireNonNull(passwordPolicy);
        this.passwordHasher = Objects.requireNonNull(passwordHasher);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    public User register(RegisterUserCommand command) {
        Email email = new Email(command.email());

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already in use");
        }

        // Assuming PasswordPolicy has isValid(String); if you changed it to validate(...),
        // adjust this call accordingly.
        if (!passwordPolicy.isValid(command.password())) {
            throw new IllegalArgumentException("Password does not meet policy requirements");
        }

        var hashedPassword = passwordHasher.hash(command.password());

        // Create a new UserId
        UserId id = new UserId(UUID.randomUUID());

        // Default privacy settings (you can tweak these defaults)
        PrivacySettings privacySettings = new PrivacySettings(
            true,  // showProfile
            true,  // showActivity
            true   // showWishlist
        );

        // Initial profile with displayName + default privacy; avatar/bio/location null for now
        UserProfile initialProfile = new UserProfile(
            command.displayName(),
            null,   // avatarUrl
            null,   // bio
            null,   // location
            privacySettings
        );

        // Use the aggregate factory; factory sets initial status + events
        User user = User.register(
            id,
            email,
            hashedPassword,
            initialProfile
        );

        User saved = userRepository.save(user);

        // Publish all domain events raised by the aggregate, then clear them
        eventPublisher.publishAll(saved.domainEvents());
        saved.clearDomainEvents();

        return saved;
    }
}
