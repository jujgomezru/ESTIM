package com.estim.javaapi.application.payment;

/**
 * Input data for adding a payment method to a user.
 */
public record AddPaymentMethodCommand(
    String userId,
    String provider,
    String externalToken,
    String last4,
    boolean isDefault
) {}
