package com.estim.javaapi.domain.user;

import java.util.Objects;

public class PaymentMethod {

    private final PaymentMethodId id;
    private final PaymentProvider provider;
    private final String externalToken;
    private final String last4;
    private final boolean isDefault;

    public PaymentMethod(PaymentMethodId id,
                         PaymentProvider provider,
                         String externalToken,
                         String last4,
                         boolean isDefault) {

        this.id = Objects.requireNonNull(id, "id must not be null");
        this.provider = Objects.requireNonNull(provider, "provider must not be null");
        this.externalToken = Objects.requireNonNull(externalToken, "externalToken must not be null");
        this.last4 = Objects.requireNonNull(last4, "last4 must not be null");
        this.isDefault = isDefault;
    }

    /**
     * Factory method used when adding a new payment method for a user.
     * Generates a fresh PaymentMethodId and enforces basic invariants.
     */
    public static PaymentMethod newMethod(
        PaymentProvider provider,
        String externalToken,
        String last4,
        boolean isDefault
    ) {
        return new PaymentMethod(
            PaymentMethodId.newId(),
            provider,
            externalToken,
            last4,
            isDefault
        );
    }

    public PaymentMethodId id() {
        return id;
    }

    public PaymentProvider provider() {
        return provider;
    }

    public String externalToken() {
        return externalToken;
    }

    public String last4() {
        return last4;
    }

    public boolean isDefault() {
        return isDefault;
    }
}
