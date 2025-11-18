package com.estim.javaapi.controllers;

import com.estim.javaapi.application.library.AddGameToLibraryCommand;
import com.estim.javaapi.application.library.AddGameToLibraryService;
import com.estim.javaapi.application.library.ListUserLibraryQuery;
import com.estim.javaapi.application.library.ListUserLibraryService;
import com.estim.javaapi.application.library.UpdateLibraryEntryCommand;
import com.estim.javaapi.application.library.UpdateLibraryEntryService;
import com.estim.javaapi.domain.library.GameId;
import com.estim.javaapi.domain.library.LibraryEntrySource;
import com.estim.javaapi.domain.user.UserId;
import com.estim.javaapi.presentation.common.ErrorResponse;
import com.estim.javaapi.presentation.library.AddGameToLibraryRequest;
import com.estim.javaapi.presentation.library.LibraryEntryResponse;
import com.estim.javaapi.presentation.library.LibraryMapper;
import com.estim.javaapi.presentation.library.UpdateLibraryEntryRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@RestController
public class LibraryController {

    private final ListUserLibraryService listUserLibraryService;
    private final UpdateLibraryEntryService updateLibraryEntryService;
    private final AddGameToLibraryService addGameToLibraryService;

    public LibraryController(ListUserLibraryService listUserLibraryService,
                             UpdateLibraryEntryService updateLibraryEntryService,
                             AddGameToLibraryService addGameToLibraryService) {
        this.listUserLibraryService = listUserLibraryService;
        this.updateLibraryEntryService = updateLibraryEntryService;
        this.addGameToLibraryService = addGameToLibraryService;
    }

    /**
     * GET /me/library
     * Returns the list of games in the current user's library.
     */
    @GetMapping("/me/library")
    public ResponseEntity<?> getMyLibrary(
        @AuthenticationPrincipal UserId currentUserId
    ) {
        if (currentUserId == null) {
            return ResponseEntity.status(401)
                .body(new ErrorResponse("UNAUTHORIZED", "Not authenticated", null));
        }

        var query = new ListUserLibraryQuery(currentUserId);

        List<LibraryEntryResponse> response =
            listUserLibraryService.listUserLibrary(query)
                .stream()
                .map(LibraryMapper::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * POST /me/library
     * Debug / admin-style endpoint to manually add a game to the current user's library.
     * In normal flows this should be triggered by a purchase event.
     */
    @PostMapping("/me/library")
    public ResponseEntity<?> addGameToLibrary(
        @AuthenticationPrincipal UserId currentUserId,
        @RequestBody AddGameToLibraryRequest request
    ) {
        try {
            if (currentUserId == null) {
                return ResponseEntity.status(401)
                    .body(new ErrorResponse("UNAUTHORIZED", "Not authenticated", null));
            }

            if (request.gameId() == null) {
                throw new IllegalArgumentException("gameId must not be null");
            }

            // Default to PURCHASE if source is null
            String rawSource = request.source();
            LibraryEntrySource source =
                (rawSource == null || rawSource.isBlank())
                    ? LibraryEntrySource.PURCHASE
                    : LibraryEntrySource.valueOf(rawSource.toUpperCase(Locale.ROOT));

            var command = new AddGameToLibraryCommand(
                currentUserId,
                GameId.of(request.gameId()),
                source
            );

            var entry = addGameToLibraryService.addGameToLibrary(command);

            return ResponseEntity.status(201).body(LibraryMapper.toResponse(entry));

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("LIBRARY_ADD_FAILED", ex.getMessage(), null));
        }
    }

    /**
     * PATCH /me/library/{gameId}
     * Intended to update library metadata such as playtime, status, tags.
     * Currently still a stub, depending on schema.
     */
    @PatchMapping("/me/library/{gameId}")
    public ResponseEntity<?> updateMyLibraryEntry(
        @AuthenticationPrincipal UserId currentUserId,
        @PathVariable("gameId") UUID gameId,
        @RequestBody UpdateLibraryEntryRequest request
    ) {
        try {
            if (currentUserId == null) {
                return ResponseEntity.status(401)
                    .body(new ErrorResponse("UNAUTHORIZED", "Not authenticated", null));
            }

            var command = new UpdateLibraryEntryCommand(
                currentUserId,
                GameId.of(gameId),
                request.additionalPlayTimeMinutes(),
                request.tags(),
                request.status()
            );

            var updated = updateLibraryEntryService.updateLibraryEntry(command);

            return ResponseEntity.ok(LibraryMapper.toResponse(updated));

        } catch (UnsupportedOperationException ex) {
            return ResponseEntity.status(501)
                .body(new ErrorResponse("LIBRARY_UPDATE_NOT_IMPLEMENTED", ex.getMessage(), null));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("LIBRARY_UPDATE_FAILED", ex.getMessage(), null));
        }
    }
}
