package store.book.bookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import store.book.bookstore.dto.OrderDto;
import store.book.bookstore.dto.OrderItemDto;
import store.book.bookstore.dto.PlaceOrderRequestDto;
import store.book.bookstore.dto.UpdateOrderStatusDto;
import store.book.bookstore.model.User;
import store.book.bookstore.service.OrderService;

@Tag(name = "Orders", description = "Endpoints for managing orders")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Place an order",
            description = "Place an order from the current user's shopping cart"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto placeOrder(
            @Parameter(description = "Order request with shipping address", required = true)
            @Valid @RequestBody PlaceOrderRequestDto requestDto,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        return orderService.placeOrder(user.getId(), requestDto);
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Get order history",
            description = "Retrieve the current user's order history"
    )
    @GetMapping
    public Page<OrderDto> getOrderHistory(
            Authentication authentication,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable
    ) {
        User user = (User) authentication.getPrincipal();
        return orderService.getOrderHistory(user.getId(), pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update order status",
            description = "Update the status of an order (Admin only)"
    )
    @PatchMapping("/{id}")
    public OrderDto updateOrderStatus(
            @Parameter(description = "ID of the order to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "New status for the order", required = true)
            @Valid @RequestBody UpdateOrderStatusDto requestDto
    ) {
        return orderService.updateOrderStatus(id, requestDto);
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Get order items",
            description = "Retrieve all items for a specific order"
    )
    @GetMapping("/{orderId}/items")
    public Page<OrderItemDto> getOrderItems(
            @Parameter(description = "ID of the order", required = true)
            @PathVariable Long orderId,
            Authentication authentication,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable
    ) {
        User user = (User) authentication.getPrincipal();
        return orderService.getOrderItems(user.getId(), orderId, pageable);
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Get a specific order item",
            description = "Retrieve a specific item from an order"
    )
    @GetMapping("/{orderId}/items/{itemId}")
    public OrderItemDto getOrderItem(
            @Parameter(description = "ID of the order", required = true)
            @PathVariable Long orderId,
            @Parameter(description = "ID of the order item", required = true)
            @PathVariable Long itemId,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        return orderService.getOrderItem(user.getId(), orderId, itemId);
    }
}
