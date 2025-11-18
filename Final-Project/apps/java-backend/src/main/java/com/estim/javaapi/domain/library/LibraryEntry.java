package com.estim.javaapi.domain.library;

import com.estim.javaapi.domain.user.UserId;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Aggregate root representing a game in a user's virtual library.
 */
public class LibraryEntry {

    private final LibraryEntryId id;
    private final UserId userId;
    private final GameId gameId;
    private final Instant ownedSince;

    private long playTimeMinutes;
    private Instant lastPlayedAt;
    private LibraryEntryStatus status;
    private Set<String> tags;

    public LibraryEntry(LibraryEntryId id,
                        UserId userId,
                        GameId gameId,
                        Instant ownedSince,
                        long playTimeMinutes,
                        Instant lastPlayedAt,
                        LibraryEntryStatus status,
                        Set<String> tags) {

        this.id = Objects.requireNonNull(id, "id must not be null");
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.gameId = Objects.requireNonNull(gameId, "gameId must not be null");
        this.ownedSince = Objects.requireNonNull(ownedSince, "ownedSince must not be null");

        if (playTimeMinutes < 0) {
            throw new IllegalArgumentException("playTimeMinutes must be >= 0");
        }
        this.playTimeMinutes = playTimeMinutes;
        this.lastPlayedAt = lastPlayedAt;
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.tags = (tags == null) ? new HashSet<>() : new HashSet<>(tags);
    }

    public static LibraryEntry newOwnedEntry(UserId userId, GameId gameId, Instant ownedSince) {
        return new LibraryEntry(
            LibraryEntryId.randomId(),
            userId,
            gameId,
            ownedSince,
            0L,
            null,
            LibraryEntryStatus.OWNED,
            new HashSet<>()
        );
    }

    public LibraryEntryId getId() {
        return id;
    }

    public UserId getUserId() {
        return userId;
    }

    public GameId getGameId() {
        return gameId;
    }

    public Instant getOwnedSince() {
        return ownedSince;
    }

    public long getPlayTimeMinutes() {
        return playTimeMinutes;
    }

    public Instant getLastPlayedAt() {
        return lastPlayedAt;
    }

    public LibraryEntryStatus getStatus() {
        return status;
    }

    public Set<String> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    // --- Domain behavior ---

    /**
     * Adds playtime in minutes to this entry.
     */
    public void addPlayTime(long minutes) {
        if (minutes < 0) {
            throw new IllegalArgumentException("minutes must be >= 0");
        }
        this.playTimeMinutes += minutes;
    }

    /**
     * Updates the last played timestamp.
     */
    public void updateLastPlayed(Instant when) {
        this.lastPlayedAt = Objects.requireNonNull(when, "when must not be null");
    }

    public void markRefunded() {
        this.status = LibraryEntryStatus.REFUNDED;
    }

    public void hide() {
        this.status = LibraryEntryStatus.HIDDEN;
    }

    public void unhide() {
        this.status = LibraryEntryStatus.OWNED;
    }

    public void addTag(String tag) {
        if (tag != null && !tag.isBlank()) {
            this.tags.add(tag.trim());
        }
    }

    public void removeTag(String tag) {
        if (tag != null) {
            this.tags.remove(tag.trim());
        }
    }
}
