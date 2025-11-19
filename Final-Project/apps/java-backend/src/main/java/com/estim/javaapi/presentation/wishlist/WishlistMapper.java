package com.estim.javaapi.presentation.wishlist;

import com.estim.javaapi.domain.wishlist.WishlistItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class WishlistMapper {

    /**
     * Maps a domain WishlistItem to a response DTO.
     *
     * @param item         domain wishlist item
     * @param currentPrice current price from catalog (may be null)
     */
    public WishlistItemResponse toResponse(WishlistItem item, BigDecimal currentPrice) {
        String gameId = item.getGameId().getValue().toString();

        return new WishlistItemResponse(
            gameId,
            item.getAddedAt(),
            item.getNotificationPreferences(),
            currentPrice
        );
    }
}
