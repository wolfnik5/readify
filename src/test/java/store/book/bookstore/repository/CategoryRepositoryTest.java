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
import store.book.bookstore.model.Category;
import store.book.bookstore.util.TestDataHelper;

@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    private Category savedCategory;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();

        savedCategory = categoryRepository.save(TestDataHelper.buildCategory());
    }

    @Test
    @DisplayName("findById: existing category → returns category")
    void findById_existingCategory_returnsCategory() {
        Optional<Category> result = categoryRepository.findById(savedCategory.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo(TestDataHelper.CATEGORY_NAME);
    }

    @Test
    @DisplayName("findById: non-existing id → returns empty")
    void findById_nonExistingId_returnsEmpty() {
        Optional<Category> result = categoryRepository.findById(9999L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findAll with pageable: returns paginated categories")
    void findAll_returnsPaginatedCategories() {
        categoryRepository.save(TestDataHelper.buildCategory2());

        Page<Category> result = categoryRepository
                .findAll(PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("save: persists category with id")
    void save_persistsCategory() {
        Category saved = categoryRepository.save(TestDataHelper.buildCategory3());

        assertThat(saved.getId()).isNotNull();
        assertThat(categoryRepository.findById(saved.getId())).isPresent();
    }

    @Test
    @DisplayName("deleteById: soft delete → category not found by default query")
    void deleteById_softDelete_categoryNotFound() {
        categoryRepository.deleteById(savedCategory.getId());

        Optional<Category> result = categoryRepository.findById(savedCategory.getId());
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("existsById: existing category → returns true")
    void existsById_existingCategory_returnsTrue() {
        assertThat(categoryRepository.existsById(savedCategory.getId())).isTrue();
    }

    @Test
    @DisplayName("existsById: non-existing id → returns false")
    void existsById_nonExistingId_returnsFalse() {
        assertThat(categoryRepository.existsById(9999L)).isFalse();
    }
}
