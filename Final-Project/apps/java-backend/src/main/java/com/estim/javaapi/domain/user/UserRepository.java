package com.estim.javaapi.domain.user;

import java.util.Optional;

/**
 * Repository port for accessing and persisting User aggregates.
 * Implementations live in the infrastructure layer.
 */
public interface UserRepository {

    Optional<User> findById(UserId id);

    Optional<User> findByEmail(Email email);

    /**
     * Persists the given user aggregate.
     * Implementations should handle both insert and update.
     */
    User save(User user);

    /**
     * Returns true if a user with the given email already exists.
     */
    boolean existsByEmail(Email email);

    /**
     * Finds a user by an already linked OAuth account.
     */
    Optional<User> findByOAuthProviderAndExternalId(OAuthProvider provider, String externalUserId);
}
