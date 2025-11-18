package com.estim.javaapi.application.library;

import com.estim.javaapi.domain.library.LibraryEntry;
import com.estim.javaapi.domain.library.LibraryRepository;
import com.estim.javaapi.domain.user.UserId;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Application service for listing all library entries for a user.
 */
@Service
public class ListUserLibraryService {

    private final LibraryRepository libraryRepository;

    public ListUserLibraryService(LibraryRepository libraryRepository) {
        this.libraryRepository = libraryRepository;
    }

    public List<LibraryEntry> listUserLibrary(ListUserLibraryQuery query) {
        UserId userId = query.userId();
        return libraryRepository.findByUser(userId);
    }
}
