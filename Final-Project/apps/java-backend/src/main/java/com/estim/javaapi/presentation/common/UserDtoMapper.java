package com.estim.javaapi.presentation.common;

import com.estim.javaapi.domain.user.User;
import com.estim.javaapi.domain.user.UserProfile;
import com.estim.javaapi.domain.user.PrivacySettings;
import com.estim.javaapi.presentation.auth.AuthenticatedUserSummary;
import com.estim.javaapi.auth.CurrentUserResponse;
import com.estim.javaapi.presentation.profile.PrivacySettingsResponse;
import com.estim.javaapi.presentation.profile.UserProfileResponse;

public final class UserDtoMapper {

    private UserDtoMapper() {
        // utility class
    }

    public static CurrentUserResponse toCurrentUserResponse(User user) {
        return new CurrentUserResponse(
            user.id().toString(),
            user.email().value(),
            user.profile() != null ? user.profile().displayName() : null,
            user.profile() != null ? user.profile().avatarUrl() : null,
            user.emailVerified()
        );
    }

    public static AuthenticatedUserSummary toAuthenticatedUserSummary(User user) {
        return new AuthenticatedUserSummary(
            user.id().toString(),
            user.email().value(),
            user.profile() != null ? user.profile().displayName() : null,
            user.emailVerified()
        );
    }

    public static UserProfileResponse toUserProfileResponse(User user) {
        UserProfile profile = user.profile();
        PrivacySettings privacy = profile != null ? profile.privacySettings() : null;

        return new UserProfileResponse(
            user.id().toString(),
            profile != null ? profile.displayName() : null,
            profile != null ? profile.avatarUrl() : null,
            profile != null ? profile.bio() : null,
            profile != null ? profile.location() : null,
            toPrivacySettingsResponse(privacy)
        );
    }

    private static PrivacySettingsResponse toPrivacySettingsResponse(PrivacySettings privacy) {
        if (privacy == null) {
            return null;
        }
        return new PrivacySettingsResponse(
            privacy.showProfile(),
            privacy.showActivity(),
            privacy.showWishlist()
        );
    }
}
