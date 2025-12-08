package com.estim.javaapi.domain.user;

import java.util.Objects;

/**
 * Immutable value object representing a user's profile data.
 * Now aligned to the DB schema: displayName + avatarUrl.
 * PrivacySettings stays as a purely in-memory concept.
 */
public final class UserProfile {

    private final String displayName;
    private final String avatarUrl;
    private final PrivacySettings privacySettings;

    public UserProfile(
        String displayName,
        String avatarUrl,
        PrivacySettings privacySettings
    ) {
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
        this.privacySettings = privacySettings;
    }

    public String displayName() {
        return displayName;
    }

    public String avatarUrl() {
        return avatarUrl;
    }

    public PrivacySettings privacySettings() {
        return privacySettings;
    }

    /**
     * Creates a new UserProfile with updated fields.
     * Useful for partial updates (immutability preserved).
     */
    public UserProfile with(
        String displayName,
        String avatarUrl,
        PrivacySettings privacySettings
    ) {
        return new UserProfile(
            displayName != null ? displayName : this.displayName,
            avatarUrl != null ? avatarUrl : this.avatarUrl,
            privacySettings != null ? privacySettings : this.privacySettings
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserProfile other)) return false;
        return Objects.equals(displayName, other.displayName)
            && Objects.equals(avatarUrl, other.avatarUrl)
            && Objects.equals(privacySettings, other.privacySettings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(displayName, avatarUrl, privacySettings);
    }
}
