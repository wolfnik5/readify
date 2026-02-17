package store.book.bookstore.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.book.bookstore.dto.AddBookToCartRequestDto;
import store.book.bookstore.dto.ShoppingCartDto;
import store.book.bookstore.dto.UpdateCartItemQuantityDto;
import store.book.bookstore.exception.EntityNotFoundException;
import store.book.bookstore.mapper.CartItemMapper;
import store.book.bookstore.mapper.ShoppingCartMapper;
import store.book.bookstore.model.CartItem;
import store.book.bookstore.model.ShoppingCart;
import store.book.bookstore.model.User;
import store.book.bookstore.repository.BookRepository;
import store.book.bookstore.repository.CartItemRepository;
import store.book.bookstore.repository.ShoppingCartRepository;
import store.book.bookstore.service.ShoppingCartService;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final CartItemMapper cartItemMapper;
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final ShoppingCartMapper shoppingCartMapper;

    @Override
    public ShoppingCartDto getShoppingCart(Long userId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Could not retrieve shopping cart for user with id: " + userId
                ));
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    @Transactional
    public ShoppingCartDto addBookToCart(Long userId, AddBookToCartRequestDto requestDto) {
        ShoppingCart shoppingCart = getShoppingCartByUserId(userId);

        if (!bookRepository.existsById(requestDto.getBookId())) {
            throw new EntityNotFoundException(
                    "Book not found with id: " + requestDto.getBookId()
            );
        }

        CartItem existingCartItem = cartItemRepository
                .findByShoppingCartIdAndBookId(shoppingCart.getId(), requestDto.getBookId())
                .orElse(null);

        if (existingCartItem != null) {
            existingCartItem.setQuantity(
                    existingCartItem.getQuantity() + requestDto.getQuantity()
            );
        } else {
            CartItem cartItem = cartItemMapper.toEntity(requestDto, shoppingCart);
            shoppingCart.getCartItems().add(cartItem);
        }

        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    @Transactional
    public ShoppingCartDto updateCartItemQuantity(
            Long userId,
            Long cartItemId,
            UpdateCartItemQuantityDto requestDto
    ) {
        ShoppingCart shoppingCart = getShoppingCartByUserId(userId);
        CartItem cartItem = getCartItemById(cartItemId);

        verifyCartItemBelongsToUser(cartItem, shoppingCart, cartItemId);

        cartItem.setQuantity(requestDto.getQuantity());
        cartItemRepository.save(cartItem);

        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    @Transactional
    public void removeCartItem(Long userId, Long cartItemId) {
        ShoppingCart shoppingCart = getShoppingCartByUserId(userId);
        CartItem cartItem = getCartItemById(cartItemId);

        verifyCartItemBelongsToUser(cartItem, shoppingCart, cartItemId);

        shoppingCart.getCartItems().remove(cartItem);
    }

    @Override
    @Transactional
    public void createShoppingCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);
    }

    private ShoppingCart getShoppingCartByUserId(Long userId) {
        return shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Shopping cart not found for user with id: " + userId
                ));
    }

    private CartItem getCartItemById(Long cartItemId) {
        return cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cart item not found with id: " + cartItemId
                ));
    }

    private void verifyCartItemBelongsToUser(
            CartItem cartItem,
            ShoppingCart shoppingCart,
            Long cartItemId
    ) {
        if (!cartItem.getShoppingCart().getId().equals(shoppingCart.getId())) {
            throw new EntityNotFoundException(
                    "Cart item with id " + cartItemId + " does not belong to user's cart"
            );
        }
    }
}
