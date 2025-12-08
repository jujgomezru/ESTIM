package com.estim.javaapi.application.profile;

import com.estim.javaapi.domain.common.DomainEventPublisher;
import com.estim.javaapi.domain.user.PrivacySettings;
import com.estim.javaapi.domain.user.User;
import com.estim.javaapi.domain.user.UserId;
import com.estim.javaapi.domain.user.UserProfile;
import com.estim.javaapi.domain.user.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

/**
 * Application service for updating the current user's profile.
 *
 * It supports partial updates: any null field in the command is interpreted
 * as "keep existing value".
 */
@Component
public class UpdateUserProfileService {

    private final UserRepository userRepository;
    private final DomainEventPublisher eventPublisher;

    public UpdateUserProfileService(UserRepository userRepository,
                                    DomainEventPublisher eventPublisher) {

        this.userRepository = Objects.requireNonNull(userRepository);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    public void updateProfile(UpdateUserProfileCommand command) {
        UserId userId = new UserId(UUID.fromString(command.userId()));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserProfile existingProfile = user.profile();
        PrivacySettings existingPrivacy = existingProfile != null
            ? existingProfile.privacySettings()
            : new PrivacySettings(true, true, true);

        // ----- displayName -----
        String displayName;
        if (command.displayName() != null) {
            displayName = command.displayName();
        } else if (existingProfile != null && existingProfile.displayName() != null) {
            displayName = existingProfile.displayName();
        } else {
            throw new IllegalStateException("User profile has no display name");
        }

        // ----- avatarUrl -----
        String avatarUrl = command.avatarUrl() != null
            ? command.avatarUrl()
            : existingProfile != null ? existingProfile.avatarUrl() : null;

        // ----- privacy -----
        boolean showProfile = command.showProfile() != null
            ? command.showProfile()
            : existingPrivacy.showProfile();

        boolean showActivity = command.showActivity() != null
            ? command.showActivity()
            : existingPrivacy.showActivity();

        boolean showWishlist = command.showWishlist() != null
            ? command.showWishlist()
            : existingPrivacy.showWishlist();

        PrivacySettings newPrivacy = new PrivacySettings(
            showProfile,
            showActivity,
            showWishlist
        );

        // New UserProfile only has displayName, avatarUrl, privacySettings
        UserProfile newProfile = new UserProfile(
            displayName,
            avatarUrl,
            newPrivacy
        );

        user.updateProfile(newProfile);

        userRepository.save(user);

        eventPublisher.publishAll(user.domainEvents());
        user.clearDomainEvents();
    }
}
