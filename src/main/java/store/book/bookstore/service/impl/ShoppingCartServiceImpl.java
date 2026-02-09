package store.book.bookstore.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.book.bookstore.dto.AddBookToCartRequestDto;
import store.book.bookstore.dto.ShoppingCartDto;
import store.book.bookstore.dto.UpdateCartItemQuantityDto;
import store.book.bookstore.exception.EntityNotFoundException;
import store.book.bookstore.mapper.ShoppingCartMapper;
import store.book.bookstore.model.Book;
import store.book.bookstore.model.CartItem;
import store.book.bookstore.model.ShoppingCart;
import store.book.bookstore.model.User;
import store.book.bookstore.repository.BookRepository;
import store.book.bookstore.repository.CartItemRepository;
import store.book.bookstore.repository.ShoppingCartRepository;
import store.book.bookstore.repository.UserRepository;
import store.book.bookstore.service.ShoppingCartService;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
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
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Could not add book because shopping cart was not found for user with id: "
                                + userId
                ));

        Book book = bookRepository.findById(requestDto.getBookId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Book not found with id: " + requestDto.getBookId()
                ));

        // Check if the book is already in the cart
        CartItem existingCartItem = cartItemRepository
                .findByShoppingCartIdAndBookId(shoppingCart.getId(), book.getId())
                .orElse(null);

        if (existingCartItem != null) {
            // Update quantity if book already exists in cart
            existingCartItem.setQuantity(
                    existingCartItem.getQuantity() + requestDto.getQuantity()
            );
            cartItemRepository.save(existingCartItem);
        } else {
            // Create new cart item
            CartItem cartItem = new CartItem();
            cartItem.setShoppingCart(shoppingCart);
            cartItem.setBook(book);
            cartItem.setQuantity(requestDto.getQuantity());
            cartItemRepository.save(cartItem);
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
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Shopping cart not found for user with id: " + userId
                ));

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cart item not found with id: " + cartItemId
                ));

        // Verify that the cart item belongs to the user's shopping cart
        if (!cartItem.getShoppingCart().getId().equals(shoppingCart.getId())) {
            throw new EntityNotFoundException(
                    "Cart item with id " + cartItemId + " does not belong to user's cart"
            );
        }

        cartItem.setQuantity(requestDto.getQuantity());
        cartItemRepository.save(cartItem);

        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    @Transactional
    public void removeCartItem(Long userId, Long cartItemId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Shopping cart not found for user with id: " + userId
                ));

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cart item not found with id: " + cartItemId
                ));

        // Verify that the cart item belongs to the user's shopping cart
        if (!cartItem.getShoppingCart().getId().equals(shoppingCart.getId())) {
            throw new EntityNotFoundException(
                    "Cart item with id " + cartItemId + " does not belong to user's cart"
            );
        }

        shoppingCart.getCartItems().remove(cartItem);
        cartItemRepository.delete(cartItem);
    }

    @Override
    @Transactional
    public void createShoppingCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found with id: " + userId
                ));

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);
    }
}
