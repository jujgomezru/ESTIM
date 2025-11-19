package com.estim.javaapi.application.wishlist;

import com.estim.javaapi.domain.common.DomainEventPublisher;
import com.estim.javaapi.domain.wishlist.WishlistItem;
import com.estim.javaapi.domain.wishlist.WishlistRepository;
import com.estim.javaapi.domain.wishlist.events.GameAddedToWishlist;
import org.springframework.stereotype.Service;

@Service
public class AddToWishlistService {

    private final WishlistRepository wishlistRepository;
    private final DomainEventPublisher eventPublisher;

    public AddToWishlistService(WishlistRepository wishlistRepository,
                                DomainEventPublisher eventPublisher) {
        this.wishlistRepository = wishlistRepository;
        this.eventPublisher = eventPublisher;
    }

    public void addToWishlist(AddToWishlistCommand command) {
        var userId = command.getUserId();
        var gameId = command.getGameId();

        // Prevent duplicates
        if (wishlistRepository.existsByUserIdAndGameId(userId, gameId)) {
            // You can make this a no-op if you prefer idempotency
            throw new IllegalStateException("Game is already in wishlist");
        }

        WishlistItem item = WishlistItem.newItem(userId, gameId);
        wishlistRepository.save(item);

        eventPublisher.publish(new GameAddedToWishlist(userId, gameId));
    }
}
