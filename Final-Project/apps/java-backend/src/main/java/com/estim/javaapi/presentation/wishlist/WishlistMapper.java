package com.estim.javaapi.presentation.wishlist;

import com.estim.javaapi.domain.wishlist.WishlistItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class WishlistMapper {

    /**
     * Maps a domain WishlistItem to the response DTO.
     *
     * @param item            domain wishlist item
     * @param gameTitle       resolved game title (placeholder for now)
     * @param coverImageUrl   resolved game cover URL (placeholder for now)
     * @param currentPrice    current price (may be null)
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
