package store.book.bookstore.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import store.book.bookstore.dto.CategoryDto;
import store.book.bookstore.dto.CategoryRequestDto;
import store.book.bookstore.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Category toEntity(CategoryRequestDto categoryRequestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateCategoryFromDto(CategoryRequestDto categoryRequestDto,
                               @MappingTarget Category category);
}
