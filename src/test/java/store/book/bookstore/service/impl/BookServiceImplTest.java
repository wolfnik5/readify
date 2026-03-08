package store.book.bookstore.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
import org.springframework.data.jpa.domain.Specification;
import store.book.bookstore.dto.BookDto;
import store.book.bookstore.dto.BookDtoWithoutCategoryIds;
import store.book.bookstore.dto.BookSearchParametersDto;
import store.book.bookstore.dto.CreateBookRequestDto;
import store.book.bookstore.exception.EntityNotFoundException;
import store.book.bookstore.mapper.BookMapper;
import store.book.bookstore.model.Book;
import store.book.bookstore.model.Category;
import store.book.bookstore.repository.BookRepository;
import store.book.bookstore.repository.BookSpecificationBuilder;
import store.book.bookstore.repository.CategoryRepository;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Category buildCategory() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Fantasy novel");
        return category;
    }

    private Book buildBook() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Warriors: Into the Wild");
        book.setAuthor("Erin Hunter");
        book.setIsbn("9780329373528");
        book.setPrice(BigDecimal.valueOf(29.99));
        book.setCategories(Set.of(buildCategory()));
        return book;
    }

    private BookDto buildBookDto() {
        BookDto dto = new BookDto();
        dto.setId(1L);
        dto.setTitle("Warriors: Into the Wild");
        dto.setAuthor("Erin Hunter");
        dto.setIsbn("9780329373528");
        dto.setPrice(BigDecimal.valueOf(29.99));
        dto.setCategoryIds(Set.of(1L));
        return dto;
    }

    private CreateBookRequestDto buildCreateRequest() {
        CreateBookRequestDto req = new CreateBookRequestDto();
        req.setTitle("Warriors: Into the Wild");
        req.setAuthor("Erin Hunter");
        req.setIsbn("9780329373528");
        req.setPrice(BigDecimal.valueOf(29.99));
        req.setCategoryIds(Set.of(1L));
        return req;
    }

    @Test
    @DisplayName("save: valid request → returns BookDto")
    void save_validRequest_returnsBookDto() {
        CreateBookRequestDto request = buildCreateRequest();
        Book book = buildBook();
        BookDto expected = buildBookDto();
        Category category = buildCategory();

        when(bookMapper.toModel(request)).thenReturn(book);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(expected);

        BookDto actual = bookService.save(request);

        assertThat(actual).isEqualTo(expected);
        verify(bookRepository).save(book);
    }

    @Test
    @DisplayName("save: category not found → throws EntityNotFoundException")
    void save_categoryNotFound_throwsException() {
        CreateBookRequestDto request = buildCreateRequest();
        Book book = buildBook();

        when(bookMapper.toModel(request)).thenReturn(book);
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.save(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Category not found with id: 1");
    }

    @Test
    @DisplayName("findAll: returns paginated list of BookDto")
    void findAll_returnsPaginatedBookDtos() {
        Pageable pageable = PageRequest.of(0, 10);
        Book book = buildBook();
        BookDto dto = buildBookDto();
        Page<Book> bookPage = new PageImpl<>(List.of(book));

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(dto);

        Page<BookDto> result = bookService.findAll(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(dto);
    }

    @Test
    @DisplayName("findById: existing id → returns BookDto")
    void findById_existingId_returnsBookDto() {
        Book book = buildBook();
        BookDto expected = buildBookDto();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(expected);

        BookDto actual = bookService.findById(1L);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("findById: non-existing id → throws EntityNotFoundException")
    void findById_nonExistingId_throwsException() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.findById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("deleteById: existing id → deletes successfully")
    void deleteById_existingId_deletesSuccessfully() {
        when(bookRepository.existsById(1L)).thenReturn(true);

        bookService.deleteById(1L);

        verify(bookRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteById: non-existing id → throws EntityNotFoundException")
    void deleteById_nonExistingId_throwsException() {
        when(bookRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> bookService.deleteById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");

        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("update: existing id → returns updated BookDto")
    void update_existingId_returnsUpdatedBookDto() {
        CreateBookRequestDto request = buildCreateRequest();
        request.setTitle("Warriors: Into the Wild Updated");
        Book book = buildBook();
        BookDto expected = buildBookDto();
        expected.setTitle("Warriors: Into the Wild Updated");
        Category category = buildCategory();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(expected);

        BookDto actual = bookService.update(1L, request);

        assertThat(actual.getTitle()).isEqualTo("Warriors: Into the Wild Updated");
        verify(bookMapper).updateBookFromDto(request, book);
    }



    @Test
    @DisplayName("search: returns filtered paginated results")
    void search_validParams_returnsFilteredPage() {
        Pageable pageable = PageRequest.of(0, 10);
        BookSearchParametersDto params = new BookSearchParametersDto(
                "Warriors", null, null, null, null);
        Book book = buildBook();
        BookDto dto = buildBookDto();
        Specification<Book> spec = (root, query, cb) -> cb.conjunction();
        Page<Book> bookPage = new PageImpl<>(List.of(book));

        when(bookSpecificationBuilder.build(params)).thenReturn(spec);
        when(bookRepository.findAll(spec, pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(dto);

        Page<BookDto> result = bookService.search(params, pageable);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("findAllByCategoryId: returns books for given category")
    void findAllByCategoryId_returnsBooks() {
        Pageable pageable = PageRequest.of(0, 10);
        Book book = buildBook();
        BookDtoWithoutCategoryIds dto = new BookDtoWithoutCategoryIds();
        dto.setId(1L);
        Page<Book> bookPage = new PageImpl<>(List.of(book));

        when(bookRepository.findAllByCategoriesId(1L, pageable)).thenReturn(bookPage);
        when(bookMapper.toDtoWithoutCategories(book)).thenReturn(dto);

        Page<BookDtoWithoutCategoryIds> result = bookService.findAllByCategoryId(1L, pageable);

        assertThat(result.getContent()).hasSize(1);
    }
}