package store.book.bookstore.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import store.book.bookstore.model.Book;
import store.book.bookstore.model.Category;
import store.book.bookstore.util.TestDataHelper;

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

        savedCategory = categoryRepository.save(TestDataHelper.buildCategory());
        savedBook = bookRepository.save(TestDataHelper.buildBook(savedCategory));
    }

    @Test
    @DisplayName("findById: existing book → returns book")
    void findById_existingBook_returnsBook() {
        Optional<Book> result = bookRepository.findById(savedBook.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo(TestDataHelper.BOOK_TITLE);
        assertThat(result.get().getAuthor()).isEqualTo(TestDataHelper.BOOK_AUTHOR);
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
        assertThat(result.getContent().getFirst().getTitle()).isEqualTo(TestDataHelper.BOOK_TITLE);
    }

    @Test
    @DisplayName("findAllByCategoriesId: wrong category → returns empty page")
    void findAllByCategoriesId_wrongCategory_returnsEmpty() {
        Page<Book> result = bookRepository
                .findAllByCategoriesId(99L, PageRequest.of(0, 20));

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @DisplayName("save: persists book and assigns id")
    void save_persistsBook() {
        Book book = TestDataHelper.buildBook2(savedCategory);

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
