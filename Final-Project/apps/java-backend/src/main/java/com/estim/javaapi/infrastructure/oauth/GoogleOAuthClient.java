package com.estim.javaapi.infrastructure.oauth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Minimal integration client for Google OAuth.
 *
 * Given a Google OAuth access token, this client can fetch basic user info
 * from the Google UserInfo endpoint.
 */
@Component
public class GoogleOAuthClient {

    private final String userInfoEndpoint;
    private final ObjectMapper objectMapper;

    public GoogleOAuthClient(
        @Value("${security.oauth.google.userinfo-endpoint:https://www.googleapis.com/oauth2/v3/userinfo}")
        String userInfoEndpoint,
        ObjectMapper objectMapper
    ) {
        this.userInfoEndpoint = Objects.requireNonNull(userInfoEndpoint);
        this.objectMapper = Objects.requireNonNull(objectMapper);
    }

    public OAuthUserInfo fetchUserInfo(String accessToken) {
        Objects.requireNonNull(accessToken, "accessToken must not be null");

        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(userInfoEndpoint).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Accept", "application/json");

            int status = conn.getResponseCode();
            if (status != 200) {
                throw new RuntimeException("Google userinfo call failed with HTTP status: " + status);
            }

            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }

            String json = sb.toString();
            JsonNode root = objectMapper.readTree(json);

            String externalUserId = root.path("sub").asText();
            String email = root.path("email").asText(null);
            String displayName = root.path("name").asText("Google User");

            if (externalUserId == null || externalUserId.isBlank()) {
                throw new RuntimeException("Google userinfo missing 'sub' field");
            }

            return new OAuthUserInfo(externalUserId, email, displayName);

        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch Google user info", e);
        }
    }
}
