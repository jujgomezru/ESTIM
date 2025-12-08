package com.estim.javaapi.presentation.auth;

public record OAuthLinkRequest(
    String provider,
    String externalToken,
    String redirectUri
) {}
