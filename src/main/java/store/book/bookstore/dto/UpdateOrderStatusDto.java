package store.book.bookstore.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import store.book.bookstore.model.Order;

@Data
public class UpdateOrderStatusDto {
    @NotNull(message = "Status is required")
    private Order.Status status;
}
