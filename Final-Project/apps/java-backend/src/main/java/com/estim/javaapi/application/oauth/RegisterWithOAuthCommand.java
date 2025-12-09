package com.estim.javaapi.application.oauth;

/**
 * Input for registering a new user via OAuth.
 *
 * For now, we get the Google access token and provider from the frontend.
 * Display name is derived from Google or from the email later.
 */
public record RegisterWithOAuthCommand(
    String provider,    // "GOOGLE"
    String accessToken  // Google access token
) {}
