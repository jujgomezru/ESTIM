package com.estim.javaapi.presentation.library;

import com.estim.javaapi.domain.library.LibraryEntry;
import com.estim.javaapi.domain.library.LibraryEntrySource;

public final class LibraryMapper {

    private LibraryMapper() {
    }

    public static LibraryEntryResponse toResponse(LibraryEntry entry) {
        LibraryEntrySource source = entry.getSource();

        return new LibraryEntryResponse(
            entry.getId().getValue(),
            entry.getGameId().getValue(),
            source != null ? source.name() : null,
            entry.getAddedAt()
        );
    }
}
