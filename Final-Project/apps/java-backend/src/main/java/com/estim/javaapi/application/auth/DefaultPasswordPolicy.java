package com.estim.javaapi.application.auth;

import org.springframework.stereotype.Component;

/**
 * Simple implementation of PasswordPolicy.
 * Adjust the rules to match your requirements.
 */
@Component
public class DefaultPasswordPolicy implements PasswordPolicy {

    @Override
    public boolean isValid(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            return false;
        }

        if (rawPassword.length() < 8) {
            return false;
        }

        // Optional: add more rules if you want:
        // boolean hasDigit = rawPassword.chars().anyMatch(Character::isDigit);
        // boolean hasUpper = rawPassword.chars().anyMatch(Character::isUpperCase);
        // return hasDigit && hasUpper;

        return true;
    }

    @Override
    public String description() {
        return "Password must be at least 8 characters long.";
    }
}
