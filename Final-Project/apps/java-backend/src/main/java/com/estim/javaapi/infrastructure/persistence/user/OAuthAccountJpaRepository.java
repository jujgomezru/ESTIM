package com.estim.javaapi.infrastructure.persistence.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.estim.javaapi.domain.user.OAuthProvider;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OAuthAccountJpaRepository extends JpaRepository<OAuthAccountJpaEntity, UUID> {

    Optional<OAuthAccountJpaEntity> findByProviderAndExternalUserId(
        OAuthProvider provider,
        String externalUserId
    );

    List<OAuthAccountJpaEntity> findByUserId(UUID userId);
}
