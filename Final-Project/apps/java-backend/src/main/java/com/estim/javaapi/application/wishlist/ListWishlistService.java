package com.estim.javaapi.application.wishlist;

import com.estim.javaapi.domain.wishlist.WishlistItem;
import com.estim.javaapi.domain.wishlist.WishlistRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListWishlistService {

    private final WishlistRepository wishlistRepository;

    public ListWishlistService(WishlistRepository wishlistRepository) {
        this.wishlistRepository = wishlistRepository;
    }

    public List<WishlistItem> listWishlist(ListWishlistForUserQuery query) {
        return wishlistRepository.findByUserId(query.getUserId());
    }
}
