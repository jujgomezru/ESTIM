package com.estim.javaapi.domain.user;

import java.util.List;
import java.util.Optional;

public interface OAuthAccountRepository {

    Optional<OAuthAccount> findByProviderAndExternalUserId(
        OAuthProvider provider,
        String externalUserId
    );

    List<OAuthAccount> findByUserId(UserId userId);

    OAuthAccount save(OAuthAccount account);
}
