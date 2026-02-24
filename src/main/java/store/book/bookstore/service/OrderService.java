package store.book.bookstore.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import store.book.bookstore.dto.OrderDto;
import store.book.bookstore.dto.OrderItemDto;
import store.book.bookstore.dto.PlaceOrderRequestDto;
import store.book.bookstore.dto.UpdateOrderStatusDto;

public interface OrderService {
    OrderDto placeOrder(Long userId, PlaceOrderRequestDto requestDto);

    Page<OrderDto> getOrderHistory(Long userId, Pageable pageable);

    OrderDto updateOrderStatus(Long orderId, UpdateOrderStatusDto requestDto);

    Page<OrderItemDto> getOrderItems(Long userId, Long orderId, Pageable pageable);

    OrderItemDto getOrderItem(Long userId, Long orderId, Long itemId);
}
