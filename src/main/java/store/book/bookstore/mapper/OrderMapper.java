package store.book.bookstore.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import store.book.bookstore.dto.OrderDto;
import store.book.bookstore.dto.OrderItemDto;
import store.book.bookstore.model.Order;
import store.book.bookstore.model.OrderItem;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(source = "user.id", target = "userId")
    OrderDto toDto(Order order);

    @Mapping(source = "book.id", target = "bookId")
    OrderItemDto orderItemToDto(OrderItem orderItem);
}
