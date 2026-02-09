package store.book.bookstore.service;

import store.book.bookstore.dto.AddBookToCartRequestDto;
import store.book.bookstore.dto.ShoppingCartDto;
import store.book.bookstore.dto.UpdateCartItemQuantityDto;

public interface ShoppingCartService {
    ShoppingCartDto getShoppingCart(Long userId);

    ShoppingCartDto addBookToCart(Long userId, AddBookToCartRequestDto requestDto);

    ShoppingCartDto updateCartItemQuantity(
            Long userId,
            Long cartItemId,
            UpdateCartItemQuantityDto requestDto
    );

    void removeCartItem(Long userId, Long cartItemId);

    void createShoppingCart(Long userId);
}
