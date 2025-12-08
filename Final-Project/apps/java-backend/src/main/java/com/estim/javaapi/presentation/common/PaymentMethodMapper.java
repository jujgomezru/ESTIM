package com.estim.javaapi.presentation.common;

import com.estim.javaapi.domain.user.PaymentMethod;
import com.estim.javaapi.presentation.payment.PaymentMethodResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public final class PaymentMethodMapper {

    public PaymentMethodMapper() {
    }

    /**
     * Instance method used by controllers and services.
     */
    public PaymentMethodResponse toResponse(PaymentMethod method) {
        return map(method);
    }

    /**
     * Instance method to map a list of domain objects to DTOs.
     */
    public List<PaymentMethodResponse> toResponseList(List<PaymentMethod> methods) {
        return methods.stream()
            .map(this::toResponse)
            .toList();
    }
    /**
     * Shared mapping logic.
     */
    private static PaymentMethodResponse map(PaymentMethod method) {
        return new PaymentMethodResponse(
            method.id().toString(),
            method.provider().name(),
            method.last4(),
            method.isDefault()
        );
    }
}
