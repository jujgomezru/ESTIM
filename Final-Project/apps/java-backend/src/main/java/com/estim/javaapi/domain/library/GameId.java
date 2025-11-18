package com.estim.javaapi.domain.library;

import java.util.Objects;

/**
 * Value object representing a game identifier from the catalog service.
 */
public final class GameId {

    private final String value;

    public GameId(String value) {
        this.value = Objects.requireNonNull(value, "value must not be null");
    }

    public String getValue() {
        return value;
    }

    public static GameId of(String value) {
        return new GameId(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameId that)) return false;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "GameId{" +
            "value='" + value + '\'' +
            '}';
    }
}
