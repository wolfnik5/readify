package store.book.bookstore.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import store.book.bookstore.model.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @EntityGraph(attributePaths = {"book", "shoppingCart"})
    Optional<CartItem> findByShoppingCartIdAndBookId(
            @Param("cartId") Long cartId,
            @Param("bookId") Long bookId
    );
}
