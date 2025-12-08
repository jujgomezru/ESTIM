package com.estim.javaapi.presentation.payment;

public record PaymentMethodRequest(
    String provider,
    String token,
    String last4,
    boolean isDefault
) {}
