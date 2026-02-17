package store.book.bookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import store.book.bookstore.dto.AddBookToCartRequestDto;
import store.book.bookstore.dto.ShoppingCartDto;
import store.book.bookstore.dto.UpdateCartItemQuantityDto;
import store.book.bookstore.model.User;
import store.book.bookstore.service.ShoppingCartService;

@Tag(name = "Shopping Cart", description = "Endpoints for managing shopping cart")
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Get shopping cart",
            description = "Retrieve the current user's shopping cart with all items"
    )
    @GetMapping
    public ShoppingCartDto getShoppingCart(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.getShoppingCart(user.getId());
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Add book to cart",
            description = "Add a book to the shopping cart with specified quantity"
    )
    @PostMapping
    public ShoppingCartDto addBookToCart(
            @Parameter(description = "Book and quantity to add", required = true)
            @Valid @RequestBody AddBookToCartRequestDto requestDto,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.addBookToCart(user.getId(), requestDto);
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Update cart item quantity",
            description = "Update the quantity of a specific item in the shopping cart"
    )
    @PutMapping("/items/{cartItemId}")
    public ShoppingCartDto updateCartItemQuantity(
            @Parameter(description = "ID of the cart item to update", required = true)
            @PathVariable Long cartItemId,
            @Parameter(description = "New quantity for the cart item", required = true)
            @Valid @RequestBody UpdateCartItemQuantityDto requestDto,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.updateCartItemQuantity(
                user.getId(),
                cartItemId,
                requestDto
        );
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Remove item from cart",
            description = "Remove a specific item from the shopping cart"
    )
    @DeleteMapping("/items/{cartItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCartItem(
            @Parameter(description = "ID of the cart item to remove", required = true)
            @PathVariable Long cartItemId,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        shoppingCartService.removeCartItem(user.getId(), cartItemId);
    }
}
