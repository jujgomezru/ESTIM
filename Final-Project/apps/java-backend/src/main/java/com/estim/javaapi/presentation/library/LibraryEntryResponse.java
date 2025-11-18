package com.estim.javaapi.presentation.library;

import java.time.Instant;
import java.util.UUID;

/**
 * API response representing a game in the user's library.
 *
 * NOTE: This matches the current DB schema (libraries table).
 * Fields like playtime/status/tags can be added later when the schema grows.
 */
public record LibraryEntryResponse(
    UUID id,
    UUID gameId,
    String source,
    Instant addedAt
) {
}
