package store.book.bookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.book.bookstore.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
