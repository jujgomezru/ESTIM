package com.estim.javaapi.presentation.wishlist;

import com.estim.javaapi.domain.wishlist.WishlistItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class WishlistMapper {

    /**
     * Maps a WishlistItem + game metadata to a response DTO.
     *
     * @param item            domain wishlist item
     * @param gameTitle       resolved from catalog or games table
     * @param coverImageUrl   resolved from game_media table
     * @param currentPrice    current price from catalog (may be null)
     */
    public WishlistItemResponse toResponse(
        WishlistItem item,
        String gameTitle,
        String coverImageUrl,
        BigDecimal currentPrice
    ) {

        return new WishlistItemResponse(
            item.getGameId().getValue().toString(),  // gameId
            gameTitle,                               // NEW
            coverImageUrl,                           // NEW
            item.getAddedAt(),                       // addedAt
            item.getNotificationPreferences(),       // preferences
            currentPrice                             // currentPrice
        );
    }
}
