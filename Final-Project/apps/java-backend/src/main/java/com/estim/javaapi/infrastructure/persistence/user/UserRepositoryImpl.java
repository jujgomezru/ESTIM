package com.estim.javaapi.infrastructure.persistence.user;

import com.estim.javaapi.domain.user.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper = new UserMapper();

    public UserRepositoryImpl(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public Optional<User> findById(UserId id) {
        return userJpaRepository.findById(id.value())
            .map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return userJpaRepository.findByEmail(email.value())
            .map(userMapper::toDomain);
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = userMapper.toEntity(user);
        UserJpaEntity saved = userJpaRepository.save(entity);
        return userMapper.toDomain(saved);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return userJpaRepository.existsByEmail(email.value());
    }

    @Override
    public Optional<User> findByOAuthProviderAndExternalId(
        OAuthProvider provider,
        String externalUserId
    ) {
        // ✅ pass the ENUM, not provider.name()
        return userJpaRepository
            .findByOAuthProviderAndExternalUserId(provider, externalUserId)
            .map(userMapper::toDomain);
    }
}
