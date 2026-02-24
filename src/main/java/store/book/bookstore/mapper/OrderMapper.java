package store.book.bookstore.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import store.book.bookstore.dto.OrderDto;
import store.book.bookstore.dto.OrderItemDto;
import store.book.bookstore.dto.PlaceOrderRequestDto;
import store.book.bookstore.model.CartItem;
import store.book.bookstore.model.Order;
import store.book.bookstore.model.OrderItem;
import store.book.bookstore.model.User;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(source = "user.id", target = "userId")
    OrderDto toDto(Order order);

    @Mapping(source = "book.id", target = "bookId")
    OrderItemDto orderItemToDto(OrderItem orderItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "total", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(source = "requestDto.shippingAddress", target = "shippingAddress")
    @Mapping(source = "user", target = "user")
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "orderDate", expression = "java(java.time.LocalDateTime.now())")
    Order toOrder(PlaceOrderRequestDto requestDto, User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(source = "book", target = "book")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "book.price", target = "price")
    OrderItem toOrderItem(CartItem cartItem);
}
