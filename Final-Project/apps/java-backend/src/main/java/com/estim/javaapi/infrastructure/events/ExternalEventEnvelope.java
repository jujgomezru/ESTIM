package com.estim.javaapi.infrastructure.events;

import java.time.Instant;

/**
 * Generic envelope for events leaving the domain boundary.
 */
public record ExternalEventEnvelope(
    String type,
    String payload,
    Instant occurredAt
) {}
