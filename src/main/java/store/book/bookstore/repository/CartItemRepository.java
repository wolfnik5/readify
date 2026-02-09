package store.book.bookstore.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import store.book.bookstore.model.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Query("SELECT ci FROM CartItem ci "
            + "WHERE ci.shoppingCart.id = :cartId AND ci.book.id = :bookId")
    Optional<CartItem> findByShoppingCartIdAndBookId(
            @Param("cartId") Long cartId,
            @Param("bookId") Long bookId
    );
}
