package com.estim.javaapi.application.payment;

/**
 * Input data for removing a payment method from a user.
 */
public record RemovePaymentMethodCommand(
    String userId,
    String paymentMethodId
) {}
