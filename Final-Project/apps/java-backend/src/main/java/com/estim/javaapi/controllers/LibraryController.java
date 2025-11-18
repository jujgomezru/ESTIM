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
import com.estim.javaapi.infrastructure.security.JwtAuthenticationProvider;
import com.estim.javaapi.infrastructure.security.SecurityContext;
import com.estim.javaapi.presentation.common.ErrorResponse;
import com.estim.javaapi.presentation.library.AddGameToLibraryRequest;
import com.estim.javaapi.presentation.library.LibraryEntryResponse;
import com.estim.javaapi.presentation.library.LibraryMapper;
import com.estim.javaapi.presentation.library.UpdateLibraryEntryRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@RestController
public class LibraryController {

    private final ListUserLibraryService listUserLibraryService;
    private final UpdateLibraryEntryService updateLibraryEntryService;
    private final AddGameToLibraryService addGameToLibraryService;
    private final JwtAuthenticationProvider authenticationProvider;

    public LibraryController(ListUserLibraryService listUserLibraryService,
                             UpdateLibraryEntryService updateLibraryEntryService,
                             AddGameToLibraryService addGameToLibraryService,
                             JwtAuthenticationProvider authenticationProvider) {
        this.listUserLibraryService = listUserLibraryService;
        this.updateLibraryEntryService = updateLibraryEntryService;
        this.addGameToLibraryService = addGameToLibraryService;
        this.authenticationProvider = authenticationProvider;
    }

    /**
     * GET /me/library
     *
     * Returns the list of games in the current user's library.
     */
    @GetMapping("/me/library")
    public ResponseEntity<?> getMyLibrary(
        @RequestHeader(name = "Authorization", required = false) String authorizationHeader
    ) {
        try {
            authenticationProvider.authenticateFromAuthorizationHeader(authorizationHeader);

            UserId userId = SecurityContext.getCurrentUserId()
                .orElseThrow(() -> new IllegalStateException("Not authenticated"));

            var query = new ListUserLibraryQuery(userId);

            List<LibraryEntryResponse> response =
                listUserLibraryService.listUserLibrary(query)
                    .stream()
                    .map(LibraryMapper::toResponse)
                    .toList();

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.status(401)
                .body(new ErrorResponse("UNAUTHORIZED", ex.getMessage(), null));
        } finally {
            authenticationProvider.clearAuthentication();
        }
    }

    /**
     * POST /me/library
     *
     * Debug / admin-style endpoint to manually add a game to the current user's library.
     * In normal flows this should be triggered by a purchase event.
     */
    @PostMapping("/me/library")
    public ResponseEntity<?> addGameToLibrary(
        @RequestHeader(name = "Authorization", required = false) String authorizationHeader,
        @RequestBody AddGameToLibraryRequest request
    ) {
        try {
            authenticationProvider.authenticateFromAuthorizationHeader(authorizationHeader);

            UserId userId = SecurityContext.getCurrentUserId()
                .orElseThrow(() -> new IllegalStateException("Not authenticated"));

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
                userId,
                GameId.of(request.gameId()),
                source
            );

            var entry = addGameToLibraryService.addGameToLibrary(command);

            // You can return 201 with or without body; here we return the created entry
            return ResponseEntity.status(201).body(LibraryMapper.toResponse(entry));

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("LIBRARY_ADD_FAILED", ex.getMessage(), null));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(401)
                .body(new ErrorResponse("UNAUTHORIZED", ex.getMessage(), null));
        } finally {
            authenticationProvider.clearAuthentication();
        }
    }

    /**
     * PATCH /me/library/{gameId}
     *
     * Intended to update library metadata such as playtime, status, tags.
     *
     * NOTE:
     * The current implementation of UpdateLibraryEntryService is a stub
     * because the 'libraries' table does not yet support these fields.
     */
    @PatchMapping("/me/library/{gameId}")
    public ResponseEntity<?> updateMyLibraryEntry(
        @RequestHeader(name = "Authorization", required = false) String authorizationHeader,
        @PathVariable("gameId") UUID gameId,
        @RequestBody UpdateLibraryEntryRequest request
    ) {
        try {
            authenticationProvider.authenticateFromAuthorizationHeader(authorizationHeader);

            UserId userId = SecurityContext.getCurrentUserId()
                .orElseThrow(() -> new IllegalStateException("Not authenticated"));

            var command = new UpdateLibraryEntryCommand(
                userId,
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
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("LIBRARY_UPDATE_FAILED", ex.getMessage(), null));
        } finally {
            authenticationProvider.clearAuthentication();
        }
    }
}
