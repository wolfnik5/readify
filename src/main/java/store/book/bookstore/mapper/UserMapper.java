package store.book.bookstore.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import store.book.bookstore.dto.UserRegistrationRequestDto;
import store.book.bookstore.dto.UserResponseDto;
import store.book.bookstore.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    User toModel(UserRegistrationRequestDto requestDto);

    UserResponseDto toDto(User user);
}
