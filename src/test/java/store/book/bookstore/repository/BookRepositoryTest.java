package store.book.bookstore.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import store.book.bookstore.model.Book;
import store.book.bookstore.model.Category;

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category savedCategory;
    private Book savedBook;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        categoryRepository.deleteAll();

        Category category = new Category();
        category.setName("Fantasy novel");
        category.setDescription("Fiction featuring magical or supernatural elements");
        savedCategory = categoryRepository.save(category);

        Book book = new Book();
        book.setTitle("Warriors: Into the Wild");
        book.setAuthor("Erin Hunter");
        book.setIsbn("9780329373528");
        book.setPrice(BigDecimal.valueOf(29.99));
        book.setCategories(Set.of(savedCategory));
        savedBook = bookRepository.save(book);
    }

    @Test
    @DisplayName("findById: existing book → returns book")
    void findById_existingBook_returnsBook() {
        Optional<Book> result = bookRepository.findById(savedBook.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Warriors: Into the Wild");
        assertThat(result.get().getAuthor()).isEqualTo("Erin Hunter");
    }

    @Test
    @DisplayName("findById: non-existing id → returns empty")
    void findById_nonExistingId_returnsEmpty() {
        Optional<Book> result = bookRepository.findById(99L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findAllByCategoriesId: returns books for given category")
    void findAllByCategoriesId_returnsMatchingBooks() {
        Page<Book> result = bookRepository.findAllByCategoriesId(
                savedCategory.getId(), PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getTitle()).isEqualTo("Warriors: Into the Wild");
    }

    @Test
    @DisplayName("findAllByCategoriesId: wrong category → returns empty page")
    void findAllByCategoriesId_wrongCategory_returnsEmpty() {
        Page<Book> result = bookRepository.findAllByCategoriesId(99L, PageRequest.of(0, 20));

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @DisplayName("save: persists book and assigns id")
    void save_persistsBook() {
        Book book = new Book();
        book.setTitle("Effective Java");
        book.setAuthor("Joshua Bloch");
        book.setIsbn("9780134685991");
        book.setPrice(BigDecimal.valueOf(39.99));
        book.setCategories(Set.of(savedCategory));

        Book saved = bookRepository.save(book);

        assertThat(saved.getId()).isNotNull();
        assertThat(bookRepository.findById(saved.getId())).isPresent();
    }

    @Test
    @DisplayName("deleteById: soft delete → book not found by default query")
    void deleteById_softDelete_bookNotFoundByDefaultQuery() {
        bookRepository.deleteById(savedBook.getId());

        Optional<Book> result = bookRepository.findById(savedBook.getId());
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("existsById: existing book → returns true")
    void existsById_existingBook_returnsTrue() {
        assertThat(bookRepository.existsById(savedBook.getId())).isTrue();
    }

    @Test
    @DisplayName("existsById: non-existing id → returns false")
    void existsById_nonExistingId_returnsFalse() {
        assertThat(bookRepository.existsById(99L)).isFalse();
    }
}
