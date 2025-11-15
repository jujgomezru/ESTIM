package com.estim.javaapi.presentation.common;

import com.estim.javaapi.domain.user.PaymentMethod;
import com.estim.javaapi.presentation.payment.PaymentMethodResponse;

public final class PaymentMethodMapper {

    private PaymentMethodMapper() {
        // utility class
    }

    public static PaymentMethodResponse toPaymentMethodResponse(PaymentMethod method) {
        return new PaymentMethodResponse(
            method.id().toString(),
            method.provider().name(),  // enum name or some code
            method.last4(),
            method.isDefault()
        );
    }
}
