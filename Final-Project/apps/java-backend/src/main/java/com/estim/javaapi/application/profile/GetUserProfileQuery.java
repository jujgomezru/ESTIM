package com.estim.javaapi.application.profile;

/**
 * Query parameters for retrieving a user's profile, taking into account
 * privacy rules and the identity of the requesting user.
 */
public record GetUserProfileQuery(
    String requesterUserId,
    String targetUserId
) {}
