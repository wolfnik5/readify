package store.book.bookstore.service.impl;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import store.book.bookstore.dto.OrderDto;
import store.book.bookstore.dto.OrderItemDto;
import store.book.bookstore.dto.PlaceOrderRequestDto;
import store.book.bookstore.dto.UpdateOrderStatusDto;
import store.book.bookstore.exception.EntityNotFoundException;
import store.book.bookstore.mapper.OrderMapper;
import store.book.bookstore.model.CartItem;
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
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Shopping cart not found for user with id: " + userId));

        if (shoppingCart.getCartItems().isEmpty()) {
            throw new IllegalStateException("Cannot place an order with an empty shopping cart");
        }

        Order order = new Order();
        order.setUser(shoppingCart.getUser());
        order.setStatus(Order.Status.PENDING);
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(requestDto.getShippingAddress());

        Set<OrderItem> orderItems = shoppingCart.getCartItems().stream()
                .map(cartItem -> createOrderItem(cartItem, order))
                .collect(Collectors.toSet());

        BigDecimal total = orderItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotal(total);
        order.setOrderItems(orderItems);
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
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order not found with id: " + orderId + " for user: " + userId));

        List<OrderItemDto> items = order.getOrderItems().stream()
                .map(orderMapper::orderItemToDto)
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), items.size());
        return new PageImpl<>(items.subList(start, end), pageable, items.size());
    }

    @Override
    public OrderItemDto getOrderItem(Long userId, Long orderId, Long itemId) {
        orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order not found with id: " + orderId + " for user: " + userId));

        OrderItem orderItem = orderItemRepository.findByIdAndOrderId(itemId, orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order item not found with id: " + itemId + " in order: " + orderId));

        return orderMapper.orderItemToDto(orderItem);
    }

    private OrderItem createOrderItem(CartItem cartItem, Order order) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setBook(cartItem.getBook());
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setPrice(cartItem.getBook().getPrice());
        return orderItem;
    }
}
