package com.estim.javaapi.presentation.profile;

public record UpdateUserProfileRequest(
    String displayName,
    String avatarUrl,
    String bio,
    String location,
    boolean showProfile,
    boolean showActivity,
    boolean showWishlist
) {}
