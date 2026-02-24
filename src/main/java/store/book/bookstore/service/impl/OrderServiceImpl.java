package store.book.bookstore.service.impl;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import store.book.bookstore.dto.OrderDto;
import store.book.bookstore.dto.OrderItemDto;
import store.book.bookstore.dto.PlaceOrderRequestDto;
import store.book.bookstore.dto.UpdateOrderStatusDto;
import store.book.bookstore.exception.EntityNotFoundException;
import store.book.bookstore.mapper.OrderMapper;
import store.book.bookstore.model.Order;
import store.book.bookstore.model.OrderItem;
import store.book.bookstore.model.ShoppingCart;
import store.book.bookstore.repository.OrderItemRepository;
import store.book.bookstore.repository.OrderRepository;
import store.book.bookstore.repository.ShoppingCartRepository;
import store.book.bookstore.service.OrderService;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderDto placeOrder(Long userId, PlaceOrderRequestDto requestDto) {
        ShoppingCart shoppingCart = getShoppingCartByUserId(userId);
        Order order = buildOrder(shoppingCart, requestDto);
        Order savedOrder = orderRepository.save(order);
        shoppingCart.getCartItems().clear();
        return orderMapper.toDto(savedOrder);
    }

    @Override
    public Page<OrderDto> getOrderHistory(Long userId, Pageable pageable) {
        return orderRepository.findAllByUserId(userId, pageable)
                .map(orderMapper::toDto);
    }

    @Override
    @Transactional
    public OrderDto updateOrderStatus(Long orderId, UpdateOrderStatusDto requestDto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order not found by id: " + orderId));
        order.setStatus(requestDto.getStatus());
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public Page<OrderItemDto> getOrderItems(Long userId, Long orderId, Pageable pageable) {
        getOrderByIdAndUserId(orderId, userId);
        return orderItemRepository.findAllByOrderIdAndOrderUserId(orderId, userId, pageable)
                .map(orderMapper::orderItemToDto);
    }

    @Override
    public OrderItemDto getOrderItem(Long userId, Long orderId, Long itemId) {
        Order order = getOrderByIdAndUserId(orderId, userId);

        OrderItem orderItem = orderItemRepository.findByIdAndOrderId(itemId, order.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order item not found with id: " + itemId
                                + " in order: " + order.getId()));

        return orderMapper.orderItemToDto(orderItem);
    }

    private ShoppingCart getShoppingCartByUserId(Long userId) {
        return shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Shopping cart not found for user with id: " + userId));
    }

    private Order getOrderByIdAndUserId(Long orderId, Long userId) {
        return orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order not found with id: " + orderId + " for user: " + userId));
    }

    private Order buildOrder(ShoppingCart shoppingCart, PlaceOrderRequestDto requestDto) {
        if (shoppingCart.getCartItems().isEmpty()) {
            throw new IllegalStateException("Cannot place an order with an empty shopping cart");
        }

        Order order = orderMapper.toOrder(requestDto, shoppingCart.getUser());

        Set<OrderItem> orderItems = shoppingCart.getCartItems().stream()
                .map(cartItem -> {
                    OrderItem item = orderMapper.toOrderItem(cartItem);
                    item.setOrder(order);
                    return item;
                })
                .collect(Collectors.toSet());

        BigDecimal total = orderItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setOrderItems(orderItems);
        order.setTotal(total);
        return order;
    }
}
