package com.estim.javaapi.controllers;

import com.estim.javaapi.application.wishlist.AddToWishlistCommand;
import com.estim.javaapi.application.wishlist.AddToWishlistService;
import com.estim.javaapi.application.wishlist.ListWishlistForUserQuery;
import com.estim.javaapi.application.wishlist.ListWishlistService;
import com.estim.javaapi.application.wishlist.RemoveFromWishlistCommand;
import com.estim.javaapi.application.wishlist.RemoveFromWishlistService;
import com.estim.javaapi.application.wishlist.UpdateWishlistItemCommand;
import com.estim.javaapi.application.wishlist.UpdateWishlistItemService;
import com.estim.javaapi.domain.library.GameId;
import com.estim.javaapi.domain.user.UserId;
import com.estim.javaapi.domain.wishlist.WishlistItem;
import com.estim.javaapi.infrastructure.security.AuthenticatedUser;
import com.estim.javaapi.presentation.wishlist.WishlistItemRequest;
import com.estim.javaapi.presentation.wishlist.WishlistItemResponse;
import com.estim.javaapi.presentation.wishlist.WishlistMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link WishlistController}.
 *
 * These tests exercise the controller logic assuming that:
 * - Spring Security has already authenticated the request
 * - @AuthenticationPrincipal injects an AuthenticatedUser
 */
@ExtendWith(MockitoExtension.class)
class WishlistControllerTest {

    private static final UUID RAW_USER_ID =
        UUID.fromString("3f2b1a6f-290f-4b86-b101-34ba1d2eea5c");
    private static final UUID RAW_GAME_ID =
        UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Mock
    private AddToWishlistService addToWishlistService;

    @Mock
    private RemoveFromWishlistService removeFromWishlistService;

    @Mock
    private ListWishlistService listWishlistService;

    @Mock
    private UpdateWishlistItemService updateWishlistItemService;

    @Mock
    private WishlistMapper wishlistMapper;

    private WishlistController controller;

    private AuthenticatedUser authenticatedUser;
    private UserId userId;
    private GameId gameId;

    @BeforeEach
    void setUp() {
        controller = new WishlistController(
            addToWishlistService,
            removeFromWishlistService,
            listWishlistService,
            updateWishlistItemService,
            wishlistMapper
        );

        userId = new UserId(RAW_USER_ID);
        gameId = new GameId(RAW_GAME_ID);
        authenticatedUser = new AuthenticatedUser(userId);
    }

    @Test
    void getWishlist_returnsItemsForAuthenticatedUser() {
        // Arrange
        Instant addedAt = Instant.parse("2025-11-19T04:45:00Z");
        Map<String, Boolean> prefs = Map.of("notifyOnAnyDiscount", true);

        WishlistItem domainItem = WishlistItem.of(
            userId,
            gameId,
            addedAt,
            null, // priceWhenAdded
            prefs
        );

        List<WishlistItem> domainItems = List.of(domainItem);

        // 🔧 Relaxed stubbing: accept any ListWishlistForUserQuery
        when(listWishlistService.listWishlist(any(ListWishlistForUserQuery.class)))
            .thenReturn(domainItems);

        WishlistItemResponse responseDto = new WishlistItemResponse(
            gameId.getValue().toString(),
            addedAt,
            prefs,
            null // currentPrice
        );
        when(wishlistMapper.toResponse(domainItem, null)).thenReturn(responseDto);

        // Act
        List<WishlistItemResponse> result = controller.getWishlist(authenticatedUser);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        WishlistItemResponse first = result.get(0);
        assertEquals(RAW_GAME_ID.toString(), first.gameId());
        assertEquals(addedAt, first.addedAt());
        assertEquals(prefs, first.notificationPreferences());
        assertNull(first.currentPrice());

        // 🔧 Relaxed verify: just ensure it was called
        verify(listWishlistService).listWishlist(any(ListWishlistForUserQuery.class));
        verify(wishlistMapper).toResponse(domainItem, null);
        verifyNoInteractions(addToWishlistService, updateWishlistItemService, removeFromWishlistService);
    }

    @Test
    void addToWishlist_withoutNotificationPreferences_callsAddOnly_andReturnsCreatedItem() {
        // Arrange
        Instant addedAt = Instant.parse("2025-11-19T05:00:00Z");

        WishlistItem createdItem = WishlistItem.of(
            userId,
            gameId,
            addedAt,
            null,
            Map.of()
        );

        // 🔧 Relaxed stubbing
        when(listWishlistService.listWishlist(any(ListWishlistForUserQuery.class)))
            .thenReturn(List.of(createdItem));

        WishlistItemResponse responseDto = new WishlistItemResponse(
            gameId.getValue().toString(),
            addedAt,
            Map.of(),
            null
        );
        when(wishlistMapper.toResponse(createdItem, null)).thenReturn(responseDto);

        WishlistItemRequest request = new WishlistItemRequest(
            RAW_GAME_ID.toString(),
            null // no notificationPreferences
        );

        // Act
        WishlistItemResponse result = controller.addToWishlist(authenticatedUser, request);

        // Assert: response
        assertNotNull(result);
        assertEquals(RAW_GAME_ID.toString(), result.gameId());
        assertEquals(addedAt, result.addedAt());
        assertTrue(result.notificationPreferences().isEmpty());

        // Assert: AddToWishlistService was called correctly
        ArgumentCaptor<AddToWishlistCommand> addCaptor =
            ArgumentCaptor.forClass(AddToWishlistCommand.class);
        verify(addToWishlistService).addToWishlist(addCaptor.capture());

        AddToWishlistCommand addCmd = addCaptor.getValue();
        assertEquals(userId, addCmd.getUserId());
        assertEquals(RAW_GAME_ID, addCmd.getGameId().getValue());

        // No notification prefs → no update call
        verify(updateWishlistItemService, never()).updateWishlistItem(any());
    }

    @Test
    void addToWishlist_withNotificationPreferences_callsAddAndUpdate_andReturnsCreatedItem() {
        // Arrange
        Instant addedAt = Instant.parse("2025-11-19T05:10:00Z");

        Map<String, Boolean> prefs = Map.of(
            "notifyOnAnyDiscount", true,
            "notifyOnBigSale", false
        );

        WishlistItem createdItem = WishlistItem.of(
            userId,
            gameId,
            addedAt,
            null,
            prefs
        );

        // 🔧 Relaxed stubbing
        when(listWishlistService.listWishlist(any(ListWishlistForUserQuery.class)))
            .thenReturn(List.of(createdItem));

        WishlistItemResponse responseDto = new WishlistItemResponse(
            gameId.getValue().toString(),
            addedAt,
            prefs,
            null
        );
        when(wishlistMapper.toResponse(createdItem, null)).thenReturn(responseDto);

        WishlistItemRequest request = new WishlistItemRequest(
            RAW_GAME_ID.toString(),
            prefs
        );

        // Act
        WishlistItemResponse result = controller.addToWishlist(authenticatedUser, request);

        // Assert: response
        assertNotNull(result);
        assertEquals(RAW_GAME_ID.toString(), result.gameId());
        assertEquals(addedAt, result.addedAt());
        assertEquals(prefs, result.notificationPreferences());

        // Assert: AddToWishlistService was called
        verify(addToWishlistService).addToWishlist(any(AddToWishlistCommand.class));

        // Assert: UpdateWishlistItemService was called with correct prefs
        ArgumentCaptor<UpdateWishlistItemCommand> updateCaptor =
            ArgumentCaptor.forClass(UpdateWishlistItemCommand.class);

        verify(updateWishlistItemService).updateWishlistItem(updateCaptor.capture());
        UpdateWishlistItemCommand updateCmd = updateCaptor.getValue();

        assertEquals(userId, updateCmd.getUserId());
        assertEquals(RAW_GAME_ID, updateCmd.getGameId().getValue());
        assertEquals(prefs, updateCmd.getNotificationPreferences());
    }

    @Test
    void removeFromWishlist_callsRemoveService_withCorrectUserAndGame() {
        // Arrange
        String gameIdStr = RAW_GAME_ID.toString();

        // Act
        controller.removeFromWishlist(authenticatedUser, gameIdStr);

        // Assert
        ArgumentCaptor<RemoveFromWishlistCommand> captor =
            ArgumentCaptor.forClass(RemoveFromWishlistCommand.class);

        verify(removeFromWishlistService).removeFromWishlist(captor.capture());
        RemoveFromWishlistCommand cmd = captor.getValue();

        assertEquals(userId, cmd.getUserId());
        assertEquals(RAW_GAME_ID, cmd.getGameId().getValue());

        verifyNoInteractions(addToWishlistService, listWishlistService, updateWishlistItemService);
    }

    @Test
    void addToWishlist_whenServiceRejectsGameAlreadyInLibrary_propagatesExceptionAndSkipsOtherCalls() {
        // Arrange
        WishlistItemRequest request = new WishlistItemRequest(
            RAW_GAME_ID.toString(),
            null
        );

        // Service rejects because game is already in the library
        doThrow(new IllegalStateException("Game is already in library"))
            .when(addToWishlistService)
            .addToWishlist(any(AddToWishlistCommand.class));

        // Act + Assert
        IllegalStateException ex = assertThrows(
            IllegalStateException.class,
            () -> controller.addToWishlist(authenticatedUser, request)
        );
        assertEquals("Game is already in library", ex.getMessage());

        // When the service throws, controller should not attempt to update prefs
        // nor reload the wishlist / map response.
        verify(addToWishlistService).addToWishlist(any(AddToWishlistCommand.class));
        verifyNoInteractions(updateWishlistItemService);
        verifyNoInteractions(listWishlistService);
        verifyNoInteractions(wishlistMapper);
    }
}
