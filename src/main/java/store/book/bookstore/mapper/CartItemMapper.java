package store.book.bookstore.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import store.book.bookstore.dto.CartItemDto;
import store.book.bookstore.model.CartItem;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    CartItemDto toDto(CartItem cartItem);
}
