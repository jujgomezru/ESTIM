package com.estim.javaapi.controllers;

import com.estim.javaapi.application.library.AddGameToLibraryCommand;
import com.estim.javaapi.application.library.AddGameToLibraryService;
import com.estim.javaapi.application.library.ListUserLibraryQuery;
import com.estim.javaapi.application.library.ListUserLibraryService;
import com.estim.javaapi.application.library.UpdateLibraryEntryService;
import com.estim.javaapi.domain.library.GameId;
import com.estim.javaapi.domain.library.LibraryEntry;
import com.estim.javaapi.domain.library.LibraryEntryId;
import com.estim.javaapi.domain.library.LibraryEntrySource;
import com.estim.javaapi.domain.user.UserId;
import com.estim.javaapi.infrastructure.security.JwtAuthenticationProvider;
import com.estim.javaapi.infrastructure.security.SecurityContext;
import com.estim.javaapi.presentation.library.AddGameToLibraryRequest;
import com.estim.javaapi.presentation.library.LibraryEntryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link LibraryController}.
 */
@ExtendWith(MockitoExtension.class)
class LibraryControllerTest {

    @Mock
    private ListUserLibraryService listUserLibraryService;

    @Mock
    private UpdateLibraryEntryService updateLibraryEntryService;

    @Mock
    private AddGameToLibraryService addGameToLibraryService;

    @Mock
    private JwtAuthenticationProvider authenticationProvider;

    private LibraryController controller;

    @BeforeEach
    void setUp() {
        controller = new LibraryController(
            listUserLibraryService,
            updateLibraryEntryService,
            addGameToLibraryService,
            authenticationProvider
        );
    }

    @Test
    void getMyLibrary_returnsEntriesForAuthenticatedUser() {
        // Arrange
        UUID rawUserId = UUID.randomUUID();
        UserId userId = new UserId(rawUserId);

        UUID rawGameId = UUID.randomUUID();
        LibraryEntry entry = new LibraryEntry(
            LibraryEntryId.randomId(),
            userId,
            GameId.of(rawGameId),
            LibraryEntrySource.PURCHASE,
            Instant.parse("2025-01-01T00:00:00Z")
        );

        try (MockedStatic<SecurityContext> mockedSecurityContext = Mockito.mockStatic(SecurityContext.class)) {
            mockedSecurityContext.when(SecurityContext::getCurrentUserId)
                .thenReturn(Optional.of(userId));

            when(listUserLibraryService.listUserLibrary(new ListUserLibraryQuery(userId)))
                .thenReturn(List.of(entry));

            String authHeader = "Bearer dummy-token";

            // Act
            ResponseEntity<?> response = controller.getMyLibrary(authHeader);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());

            assertTrue(response.getBody() instanceof List);
            @SuppressWarnings("unchecked")
            List<LibraryEntryResponse> body = (List<LibraryEntryResponse>) response.getBody();

            assertNotNull(body);
            assertEquals(1, body.size());

            LibraryEntryResponse first = body.get(0);
            assertEquals(rawGameId, first.gameId());
            assertEquals("PURCHASE", first.source().toUpperCase(Locale.ROOT));
            assertEquals(entry.getAddedAt(), first.addedAt());

            verify(authenticationProvider).authenticateFromAuthorizationHeader(authHeader);
            verify(listUserLibraryService).listUserLibrary(new ListUserLibraryQuery(userId));
        }
    }

    @Test
    void addGameToLibrary_createsEntryForAuthenticatedUser() {
        // Arrange
        UUID rawUserId = UUID.randomUUID();
        UserId userId = new UserId(rawUserId);

        UUID rawGameId = UUID.randomUUID();
        LibraryEntry createdEntry = new LibraryEntry(
            LibraryEntryId.randomId(),
            userId,
            GameId.of(rawGameId),
            LibraryEntrySource.PURCHASE,
            Instant.parse("2025-02-01T00:00:00Z")
        );

        try (MockedStatic<SecurityContext> mockedSecurityContext = Mockito.mockStatic(SecurityContext.class)) {
            mockedSecurityContext.when(SecurityContext::getCurrentUserId)
                .thenReturn(Optional.of(userId));

            when(addGameToLibraryService.addGameToLibrary(any(AddGameToLibraryCommand.class)))
                .thenReturn(createdEntry);

            String authHeader = "Bearer dummy-token";
            AddGameToLibraryRequest request = new AddGameToLibraryRequest(
                rawGameId,
                "PURCHASE"
            );

            // Act
            ResponseEntity<?> response = controller.addGameToLibrary(authHeader, request);

            // Assert
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertTrue(response.getBody() instanceof LibraryEntryResponse);

            LibraryEntryResponse body = (LibraryEntryResponse) response.getBody();
            assertEquals(rawGameId, body.gameId());
            assertEquals("PURCHASE", body.source());
            assertEquals(createdEntry.getAddedAt(), body.addedAt());

            // Verify authentication was performed
            verify(authenticationProvider).authenticateFromAuthorizationHeader(authHeader);

            // Verify command sent to service
            ArgumentCaptor<AddGameToLibraryCommand> captor =
                ArgumentCaptor.forClass(AddGameToLibraryCommand.class);
            verify(addGameToLibraryService).addGameToLibrary(captor.capture());

            AddGameToLibraryCommand sentCommand = captor.getValue();
            assertEquals(userId, sentCommand.userId());
            assertEquals(rawGameId, sentCommand.gameId().getValue());
            assertEquals(LibraryEntrySource.PURCHASE, sentCommand.source());
        }
    }
}
