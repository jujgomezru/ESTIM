package com.estim.javaapi.application.wishlist;

import com.estim.javaapi.domain.common.DomainEventPublisher;
import com.estim.javaapi.domain.library.LibraryRepository;
import com.estim.javaapi.domain.wishlist.WishlistItem;
import com.estim.javaapi.domain.wishlist.WishlistRepository;
import com.estim.javaapi.domain.wishlist.events.GameAddedToWishlist;
import org.springframework.stereotype.Service;

@Service
public class AddToWishlistService {

    private final WishlistRepository wishlistRepository;
    private final LibraryRepository libraryRepository;
    private final DomainEventPublisher eventPublisher;

    public AddToWishlistService(WishlistRepository wishlistRepository,
                                LibraryRepository libraryRepository,
                                DomainEventPublisher eventPublisher) {
        this.wishlistRepository = wishlistRepository;
        this.libraryRepository = libraryRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Adds a game to the user's wishlist.
     *
     * Business rules:
     * - The game must NOT already be in the user's library.
     * - The game must NOT already be in the user's wishlist.
     */
    public void addToWishlist(AddToWishlistCommand command) {
        var userId = command.getUserId();
        var gameId = command.getGameId();

        if (libraryRepository.findByUserAndGame(userId, gameId).isPresent()) {
            throw new IllegalStateException("Game is already in library");
        }

        if (wishlistRepository.existsByUserIdAndGameId(userId, gameId)) {
            throw new IllegalStateException("Game is already in wishlist");
        }

        WishlistItem item = WishlistItem.newItem(userId, gameId);
        wishlistRepository.save(item);

        eventPublisher.publish(new GameAddedToWishlist(userId, gameId));
    }
}
