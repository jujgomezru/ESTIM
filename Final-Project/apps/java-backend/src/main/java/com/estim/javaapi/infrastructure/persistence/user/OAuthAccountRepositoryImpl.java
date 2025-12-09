package com.estim.javaapi.infrastructure.persistence.user;

import com.estim.javaapi.domain.user.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class OAuthAccountRepositoryImpl implements OAuthAccountRepository {

    private final OAuthAccountJpaRepository jpa;
    private final UserJpaRepository userJpaRepository; // needed to attach the account to a user

    public OAuthAccountRepositoryImpl(
        OAuthAccountJpaRepository jpa,
        UserJpaRepository userJpaRepository
    ) {
        this.jpa = jpa;
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public Optional<OAuthAccount> findByProviderAndExternalUserId(
        OAuthProvider provider,
        String externalUserId
    ) {
        return jpa.findByProviderAndExternalUserId(provider, externalUserId)
            .map(this::toDomain);
    }

    @Override
    public List<OAuthAccount> findByUserId(UserId userId) {
        return jpa.findByUserId(userId.value())
            .stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public OAuthAccount save(OAuthAccount account) {
        OAuthAccountJpaEntity entity = toEntity(account);
        OAuthAccountJpaEntity saved = jpa.save(entity);
        return toDomain(saved);
    }


    private OAuthAccount toDomain(OAuthAccountJpaEntity e) {
        return new OAuthAccount(
            new OAuthAccountId(e.getId()),
            new UserId(e.getUser().getId()),
            e.getProvider(),
            e.getExternalUserId(),
            e.getEmail(),
            e.getLinkedAt()
        );
    }

    private OAuthAccountJpaEntity toEntity(OAuthAccount domain) {
        OAuthAccountJpaEntity e = new OAuthAccountJpaEntity();
        e.setId(domain.id().value());

        // attach the JPA user entity
        e.setUser(
            userJpaRepository.findById(domain.userId().value())
                .orElseThrow()
        );

        e.setProvider(domain.provider());
        e.setExternalUserId(domain.externalUserId());
        e.setEmail(domain.email());
        e.setLinkedAt(domain.linkedAt());
        return e;
    }
}
