package store.book.bookstore.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import store.book.bookstore.dto.CategoryDto;
import store.book.bookstore.dto.CategoryRequestDto;
import store.book.bookstore.exception.EntityNotFoundException;
import store.book.bookstore.mapper.CategoryMapper;
import store.book.bookstore.model.Category;
import store.book.bookstore.repository.CategoryRepository;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category buildCategory() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Fantasy novel");
        category.setDescription("Fiction featuring magical or supernatural elements");
        return category;
    }

    private CategoryDto buildCategoryDto() {
        return new CategoryDto(1L, "Fantasy novel",
                "Fiction featuring magical or supernatural elements");
    }

    private CategoryRequestDto buildRequest() {
        return new CategoryRequestDto("Fantasy novel",
                "Fiction featuring magical or supernatural elements");
    }

    @Test
    @DisplayName("findAll: returns paginated CategoryDtos")
    void findAll_returnsPaginatedDtos() {
        Pageable pageable = PageRequest.of(0, 10);
        Category category = buildCategory();
        CategoryDto dto = buildCategoryDto();
        Page<Category> page = new PageImpl<>(List.of(category));

        when(categoryRepository.findAll(pageable)).thenReturn(page);
        when(categoryMapper.toDto(category)).thenReturn(dto);

        Page<CategoryDto> result = categoryService.findAll(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().id()).isEqualTo(1L);
        assertThat(result.getContent().getFirst().name()).isEqualTo("Fantasy novel");
    }

    @Test
    @DisplayName("findAll: empty repository → returns empty page")
    void findAll_emptyRepo_returnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(categoryRepository.findAll(pageable)).thenReturn(Page.empty());

        Page<CategoryDto> result = categoryService.findAll(pageable);

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @DisplayName("getById: existing id → returns CategoryDto")
    void getById_existingId_returnsCategoryDto() {
        Category category = buildCategory();
        CategoryDto expected = buildCategoryDto();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(expected);

        CategoryDto actual = categoryService.getById(1L);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("getById: non-existing id → throws EntityNotFoundException")
    void getById_nonExistingId_throwsException() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("save: valid request → returns saved CategoryDto")
    void save_validRequest_returnsCategoryDto() {
        CategoryRequestDto request = buildRequest();
        Category category = buildCategory();
        CategoryDto expected = buildCategoryDto();

        when(categoryMapper.toEntity(request)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expected);

        CategoryDto actual = categoryService.save(request);

        assertThat(actual).isEqualTo(expected);
        verify(categoryRepository).save(category);
    }

    @Test
    @DisplayName("update: existing id → returns updated CategoryDto")
    void update_existingId_returnsUpdatedDto() {
        CategoryRequestDto request = new CategoryRequestDto("Updated", "Updated desc");
        Category category = buildCategory();
        CategoryDto expected = new CategoryDto(1L, "Updated", "Updated desc");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expected);

        CategoryDto actual = categoryService.update(1L, request);

        assertThat(actual.name()).isEqualTo("Updated");
        verify(categoryMapper).updateCategoryFromDto(request, category);
    }

    @Test
    @DisplayName("update: non-existing id → throws EntityNotFoundException")
    void update_nonExistingId_throwsException() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.update(99L, buildRequest()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("deleteById: existing id → deletes successfully")
    void deleteById_existingId_deletesSuccessfully() {
        when(categoryRepository.existsById(1L)).thenReturn(true);

        categoryService.deleteById(1L);

        verify(categoryRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteById: non-existing id → throws EntityNotFoundException")
    void deleteById_nonExistingId_throwsException() {
        when(categoryRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> categoryService.deleteById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");

        verifyNoMoreInteractions(categoryRepository);
    }
}