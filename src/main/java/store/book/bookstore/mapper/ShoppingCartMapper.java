package store.book.bookstore.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import store.book.bookstore.dto.ShoppingCartDto;
import store.book.bookstore.model.ShoppingCart;

@Mapper(componentModel = "spring", uses = CartItemMapper.class)
public interface ShoppingCartMapper {
    @Mapping(source = "user.id", target = "userId")
    ShoppingCartDto toDto(ShoppingCart shoppingCart);
}
