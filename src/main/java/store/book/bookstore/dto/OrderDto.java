package store.book.bookstore.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Data;
import store.book.bookstore.model.Order;

@Data
public class OrderDto {
    private Long id;
    private Long userId;
    private Set<OrderItemDto> orderItems;
    private LocalDateTime orderDate;
    private BigDecimal total;
    private Order.Status status;
}
