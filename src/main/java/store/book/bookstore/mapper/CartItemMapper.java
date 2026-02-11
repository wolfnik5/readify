package store.book.bookstore.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import store.book.bookstore.dto.AddBookToCartRequestDto;
import store.book.bookstore.dto.CartItemDto;
import store.book.bookstore.model.Book;
import store.book.bookstore.model.CartItem;
import store.book.bookstore.model.ShoppingCart;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    CartItemDto toDto(CartItem cartItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "requestDto.bookId", target = "book", qualifiedByName = "bookFromId")
    @Mapping(source = "shoppingCart", target = "shoppingCart")
    @Mapping(source = "requestDto.quantity", target = "quantity")
    CartItem toEntity(AddBookToCartRequestDto requestDto, ShoppingCart shoppingCart);

    @Named("bookFromId")
    default Book bookFromId(Long id) {
        if (id == null) {
            return null;
        }
        Book book = new Book();
        book.setId(id);
        return book;
    }
}
